package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.ProductoRequestDTO;
import com.proyecto.dejatuhuella.model.Categoria;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.CategoriaRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaRepository categoriaRepository; // Inyectar CategoriaRepository

    @Transactional(readOnly = true)
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorVendedor(Long vendedorId) {
        return productoRepository.findByVendedorId(vendedorId);
    }

    @Transactional
    public Producto crearProducto(ProductoRequestDTO productoRequestDTO) {
        Usuario vendedor = usuarioService.obtenerUsuarioPorId(productoRequestDTO.getVendedorId())
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado con ID: " + productoRequestDTO.getVendedorId()));

        Categoria categoria = null;
        if (productoRequestDTO.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(productoRequestDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productoRequestDTO.getCategoriaId()));
        }

        Producto producto = new Producto();
        producto.setNombre(productoRequestDTO.getNombre());
        producto.setDescripcion(productoRequestDTO.getDescripcion());
        producto.setPrecio(productoRequestDTO.getPrecio());
        producto.setStock(productoRequestDTO.getStock());
        producto.setVendedor(vendedor);
        producto.setCategoria(categoria); // Asignar la categoría

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizarProducto(Long id, ProductoRequestDTO productoRequestDTO) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Validar que el vendedor que actualiza sea el propietario o un admin (esto se hace en el controller con @PreAuthorize)
        // Aquí solo actualizamos los campos.

        if (productoRequestDTO.getNombre() != null) {
            productoExistente.setNombre(productoRequestDTO.getNombre());
        }
        if (productoRequestDTO.getDescripcion() != null) {
            productoExistente.setDescripcion(productoRequestDTO.getDescripcion());
        }
        if (productoRequestDTO.getPrecio() != null) {
            productoExistente.setPrecio(productoRequestDTO.getPrecio());
        }
        if (productoRequestDTO.getStock() != null) {
            productoExistente.setStock(productoRequestDTO.getStock());
        }

        if (productoRequestDTO.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(productoRequestDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productoRequestDTO.getCategoriaId()));
            productoExistente.setCategoria(categoria);
        } else {
            // Si se pasa categoriaId como null, se podría interpretar como quitar la categoría
            productoExistente.setCategoria(null);
        }

        // El vendedor del producto no debería cambiarse fácilmente aquí.
        // Si se necesita cambiar el vendedor, debería ser una operación separada y controlada.

        return productoRepository.save(productoExistente);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        // Considerar la lógica de negocio antes de eliminar, como verificar si está en pedidos activos.
        productoRepository.deleteById(id);
    }
}