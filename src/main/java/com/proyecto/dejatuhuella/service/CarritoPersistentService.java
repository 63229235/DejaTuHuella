package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.DetallePedidoRequestDTO;
import com.proyecto.dejatuhuella.dto.PedidoRequestDTO;
import com.proyecto.dejatuhuella.model.Carrito;
import com.proyecto.dejatuhuella.model.CarritoItem;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.CarritoItemRepository;
import com.proyecto.dejatuhuella.repository.CarritoRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarritoPersistentService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene el carrito del usuario actual o crea uno nuevo si no existe
     */
    @Transactional
    public Carrito obtenerCarritoUsuarioActual() {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        return carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito(usuario);
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    /**
     * Agrega un producto al carrito
     */
    @Transactional
    public void agregarProducto(Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Carrito carrito = obtenerCarritoUsuarioActual();
        
        Optional<CarritoItem> itemExistente = carritoItemRepository.findByCarritoAndProducto(carrito, producto);
        
        if (itemExistente.isPresent()) {
            // Si el producto ya está en el carrito, actualizar la cantidad
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            carritoItemRepository.save(item);
        } else {
            // Si el producto no está en el carrito, crear un nuevo item
            CarritoItem nuevoItem = new CarritoItem(carrito, producto, cantidad);
            carritoItemRepository.save(nuevoItem);
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    @Transactional
    public void actualizarCantidad(Long productoId, Integer cantidad) {
        Carrito carrito = obtenerCarritoUsuarioActual();
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        Optional<CarritoItem> itemOpt = carritoItemRepository.findByCarritoAndProducto(carrito, producto);
        
        if (itemOpt.isPresent()) {
            if (cantidad <= 0) {
                // Si la cantidad es 0 o negativa, eliminar el item
                carritoItemRepository.delete(itemOpt.get());
            } else {
                // Actualizar la cantidad
                CarritoItem item = itemOpt.get();
                item.setCantidad(cantidad);
                carritoItemRepository.save(item);
            }
        }
    }

    /**
     * Elimina un producto del carrito
     */
    @Transactional
    public void eliminarProducto(Long productoId) {
        Carrito carrito = obtenerCarritoUsuarioActual();
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        Optional<CarritoItem> itemOpt = carritoItemRepository.findByCarritoAndProducto(carrito, producto);
        
        itemOpt.ifPresent(carritoItemRepository::delete);
    }

    /**
     * Vacía el carrito
     */
    @Transactional
    public void vaciarCarrito() {
        Carrito carrito = obtenerCarritoUsuarioActual();
        carritoItemRepository.deleteByCarrito(carrito);
    }

    /**
     * Obtiene los items del carrito con detalles
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getItemsDetallados() {
        Carrito carrito = obtenerCarritoUsuarioActual();
        List<CarritoItem> items = carritoItemRepository.findByCarrito(carrito);
        List<Map<String, Object>> itemsDetallados = new ArrayList<>();

        for (CarritoItem item : items) {
            Map<String, Object> itemDetalle = new HashMap<>();
            itemDetalle.put("producto", item.getProducto());
            itemDetalle.put("cantidad", item.getCantidad());
            itemDetalle.put("subtotal", item.getSubtotal());
            itemsDetallados.add(itemDetalle);
        }

        return itemsDetallados;
    }

    /**
     * Calcula el total del carrito
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotal() {
        Carrito carrito = obtenerCarritoUsuarioActual();
        List<CarritoItem> items = carritoItemRepository.findByCarrito(carrito);
        
        return items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Verifica si hay suficiente stock para todos los productos del carrito
     */
    @Transactional(readOnly = true)
    public boolean verificarStock() {
        Carrito carrito = obtenerCarritoUsuarioActual();
        List<CarritoItem> items = carritoItemRepository.findByCarrito(carrito);
        
        for (CarritoItem item : items) {
            Producto producto = item.getProducto();
            if (producto.getStock() < item.getCantidad()) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Genera un PedidoRequestDTO a partir del carrito
     */
    @Transactional(readOnly = true)
    public PedidoRequestDTO generarPedidoRequest(Long usuarioId) {
        Carrito carrito = obtenerCarritoUsuarioActual();
        List<CarritoItem> items = carritoItemRepository.findByCarrito(carrito);
        
        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setUsuarioId(usuarioId);
        
        List<DetallePedidoRequestDTO> detalles = new ArrayList<>();
        for (CarritoItem item : items) {
            DetallePedidoRequestDTO detalle = new DetallePedidoRequestDTO();
            detalle.setProductoId(item.getProducto().getId());
            detalle.setCantidad(item.getCantidad());
            detalles.add(detalle);
        }
        
        pedidoRequest.setDetalles(detalles);
        return pedidoRequest;
    }

    /**
     * Obtiene el usuario actual autenticado
     */
    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        
        try {
            com.proyecto.dejatuhuella.security.CustomUserDetails userDetails = 
                    (com.proyecto.dejatuhuella.security.CustomUserDetails) auth.getPrincipal();
            return userDetails.getUsuario();
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Obtiene la cantidad total de productos en el carrito
     */
    @Transactional(readOnly = true)
    public int getCantidadTotal() {
        Carrito carrito = obtenerCarritoUsuarioActual();
        List<CarritoItem> items = carritoItemRepository.findByCarrito(carrito);
        
        return items.stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }
}