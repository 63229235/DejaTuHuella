package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.DetallePedidoRequestDTO; // Importar
import com.proyecto.dejatuhuella.dto.PedidoRequestDTO; // Importar
import com.proyecto.dejatuhuella.model.DetallePedido; // Importar
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.EstadoPedido;
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

    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosPorComprador(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO pedidoRequestDTO) {
        Usuario comprador = usuarioService.obtenerUsuarioPorId(pedidoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + pedidoRequestDTO.getUsuarioId()));

        Pedido nuevoPedido = new Pedido(comprador);
        nuevoPedido.setFechaPedido(LocalDateTime.now());
        nuevoPedido.setEstado(EstadoPedido.PENDIENTE);

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
    public Pedido actualizarEstadoPedido(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        // Lógica adicional si el estado cambia a CANCELADO (ej. reponer stock)
        if (nuevoEstado == EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.CANCELADO) {
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
        if (pedido.getEstado() != EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.ENTREGADO /* u otros estados finales */) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        pedidoRepository.deleteById(id);
    }

    // Añadir estos métodos a la clase PedidoService

    public List<Pedido> obtenerPedidosDelUsuario() {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            return pedidoRepository.findByUsuario(usuario);
        }

        return List.of();
    }

    public List<Pedido> obtenerVentasDelUsuario() {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Aquí necesitarías una consulta personalizada que busque pedidos que contengan productos del vendedor
            return pedidoRepository.findVentasByUsuario(usuario.getId());
        }

        return List.of();
    }
}