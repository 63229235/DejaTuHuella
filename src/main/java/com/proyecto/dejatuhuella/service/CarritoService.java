package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.DetallePedidoRequestDTO;
import com.proyecto.dejatuhuella.dto.PedidoRequestDTO;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SessionScope // Para mantener el carrito en la sesión del usuario
public class CarritoService {

    private final Map<Long, Integer> items = new HashMap<>(); // productoId -> cantidad

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoService pedidoService;

    public void agregarProducto(Long productoId, Integer cantidad) {
        // Si ya existe el producto en el carrito, sumamos la cantidad
        items.merge(productoId, cantidad, Integer::sum);
    }

    public void actualizarCantidad(Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            items.remove(productoId);
        } else {
            items.put(productoId, cantidad);
        }
    }

    public void eliminarProducto(Long productoId) {
        items.remove(productoId);
    }

    public void vaciarCarrito() {
        items.clear();
    }

    public Map<Long, Integer> getItems() {
        return items;
    }

    public int getCantidadTotal() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    public List<Map<String, Object>> getItemsDetallados() {
        List<Map<String, Object>> itemsDetallados = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Producto producto = productoRepository.findById(productoId).orElse(null);
            if (producto != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("producto", producto);
                item.put("cantidad", cantidad);
                item.put("subtotal", producto.getPrecio().multiply(java.math.BigDecimal.valueOf(cantidad)));
                itemsDetallados.add(item);
            }
        }

        return itemsDetallados;
    }

    public java.math.BigDecimal getTotal() {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Producto producto = productoRepository.findById(productoId).orElse(null);
            if (producto != null) {
                total = total.add(producto.getPrecio().multiply(java.math.BigDecimal.valueOf(cantidad)));
            }
        }

        return total;
    }

    public boolean verificarStock() {
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Producto producto = productoRepository.findById(productoId).orElse(null);
            if (producto == null || producto.getStock() < cantidad) {
                return false;
            }
        }

        return true;
    }

    public PedidoRequestDTO generarPedidoRequest(Long usuarioId) {
        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setCompradorId(usuarioId);

        List<DetallePedidoRequestDTO> detalles = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            DetallePedidoRequestDTO detalle = new DetallePedidoRequestDTO();
            detalle.setProductoId(entry.getKey());
            detalle.setCantidad(entry.getValue());
            detalles.add(detalle);
        }

        pedidoRequest.setDetalles(detalles);
        return pedidoRequest;
    }
}