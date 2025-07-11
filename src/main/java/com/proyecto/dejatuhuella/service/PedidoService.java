package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.DetallePedidoRequestDTO;
import com.proyecto.dejatuhuella.dto.PedidoDTO;
import com.proyecto.dejatuhuella.dto.PedidoRequestDTO; // Importar
import com.proyecto.dejatuhuella.model.DetallePedido; // Importar
import com.proyecto.dejatuhuella.model.EstadoPedidoEntity;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.EstadoPedidoRepository;
import com.proyecto.dejatuhuella.repository.PedidoRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private EstadoPedidoRepository estadoPedidoRepository;

    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerTodosLosPedidosDTO() {
        return pedidoRepository.findAll().stream()
                .map(this::convertirPedidoADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<PedidoDTO> obtenerPedidoDTOPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(this::convertirPedidoADTO);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPedidosPorComprador(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirPedidoADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO pedidoRequestDTO) {
        Usuario comprador = usuarioService.obtenerUsuarioPorId(pedidoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + pedidoRequestDTO.getUsuarioId()));

        Pedido nuevoPedido = new Pedido(comprador);
        nuevoPedido.setFechaPedido(LocalDateTime.now());
        
        // Obtener el estado PENDIENTE de la base de datos
        EstadoPedidoEntity estadoPendiente = estadoPedidoRepository.findByNombreEstado("PENDIENTE");
        if (estadoPendiente == null) {
            throw new RuntimeException("Estado PENDIENTE no encontrado en la base de datos");
        }
        nuevoPedido.setEstado(estadoPendiente);

        BigDecimal totalPedido = BigDecimal.ZERO;
        Set<DetallePedido> detallesDelPedido = new HashSet<>();

        for (DetallePedidoRequestDTO detalleDTO : pedidoRequestDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getProductoId()));

            // Verificar stock
            if (producto.getStock() < detalleDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() +
                        ". Solicitado: " + detalleDTO.getCantidad() +
                        ", Disponible: " + producto.getStock());
            }

            // Crear el detalle del pedido
            DetallePedido detallePedido = new DetallePedido(
                    nuevoPedido, // Se asigna el pedido al detalle
                    producto,
                    detalleDTO.getCantidad(),
                    producto.getPrecio() // Precio actual del producto
            );
            detallesDelPedido.add(detallePedido);

            // Actualizar el stock del producto
            producto.setStock(producto.getStock() - detalleDTO.getCantidad());
            productoRepository.save(producto); // Guardar el producto con el stock actualizado

            // Sumar al total del pedido
            totalPedido = totalPedido.add(
                    producto.getPrecio().multiply(BigDecimal.valueOf(detalleDTO.getCantidad()))
            );
        }

        nuevoPedido.setDetalles(detallesDelPedido);
        nuevoPedido.setTotal(totalPedido);

        return pedidoRepository.save(nuevoPedido);
    }

    @Transactional
    public Pedido actualizarEstadoPedido(Long id, String nombreEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        EstadoPedidoEntity nuevoEstado = estadoPedidoRepository.findByNombreEstado(nombreEstado);
        if (nuevoEstado == null) {
            throw new RuntimeException("Estado no válido: " + nombreEstado);
        }

        // Lógica adicional si el estado cambia a CANCELADO (ej. reponer stock)
        if ("CANCELADO".equals(nombreEstado) && !"CANCELADO".equals(pedido.getEstado().getNombreEstado())) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        // Considerar otras transiciones de estado y sus efectos

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void eliminarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        // Si se elimina un pedido, considerar si se debe reponer el stock (si no fue cancelado antes)
        String estadoNombre = pedido.getEstado().getNombreEstado();
        if (!"CANCELADO".equals(estadoNombre) && !"ENTREGADO".equals(estadoNombre) /* u otros estados finales */) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        pedidoRepository.deleteById(id);
    }

    // Añadir estos métodos a la clase PedidoService

    public List<PedidoDTO> obtenerPedidosDelUsuario() {
        // Obtener el usuario autenticado
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                List<Pedido> pedidos = pedidoRepository.findByUsuario(usuario);
                
                // Convertir las entidades Pedido a DTOs para evitar problemas de serialización
                return pedidos.stream()
                    .map(this::convertirPedidoADTO)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // Loguear el error pero no interrumpir la carga del panel
            System.err.println("Error al obtener pedidos del usuario: " + e.getMessage());
            e.printStackTrace(); // Añadir esto para ver el stacktrace completo
        }
        
        return List.of();
    }

    // Método auxiliar para convertir un Pedido a PedidoDTO
    private PedidoDTO convertirPedidoADTO(Pedido pedido) {
        return new PedidoDTO(
            pedido.getId(),
            pedido.getFechaPedido(),
            pedido.getEstado() != null ? pedido.getEstado().getNombreEstado() : "DESCONOCIDO",
            pedido.getTotal(),
            pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : "Usuario desconocido",
            pedido.getUsuario() != null ? pedido.getUsuario().getId() : null,
            pedido.getDetalles()
        );
    }
    
    public List<PedidoDTO> obtenerVentasDelUsuario() {
        // Obtener el usuario autenticado
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Obtener los pedidos que contienen productos del vendedor
                List<Pedido> ventas = pedidoRepository.findVentasByUsuario(usuario.getId());
                
                // Convertir las entidades Pedido a DTOs para evitar problemas de serialización
                return ventas.stream()
                    .map(this::convertirPedidoADTO)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // Loguear el error pero no interrumpir la carga del panel
            System.err.println("Error al obtener ventas del usuario: " + e.getMessage());
            e.printStackTrace(); // Añadir esto para ver el stacktrace completo
        }

        // Asegurarse de siempre devolver una lista vacía en caso de error
        return List.of();
    }
    
    /**
     * Verifica si un pedido existe en la base de datos
     * @param pedidoId ID del pedido a verificar
     * @return true si el pedido existe, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean existePedido(Long pedidoId) {
        return pedidoId != null && pedidoRepository.existsById(pedidoId);
    }
}