package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.ProductoRequestDTO;
import com.proyecto.dejatuhuella.model.Categoria;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.CategoriaRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    // Cache para productos destacados
    private List<Producto> productosDestacadosCache;
    private long ultimaActualizacionCache = 0;
    private static final long INTERVALO_ACTUALIZACION_CACHE = 10 * 60 * 1000; // 10 minutos en milisegundos

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    public List<Producto> obtenerProductosPorUsuario(Long usuarioId) {
        return productoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Producto crearProducto(ProductoRequestDTO productoRequestDTO) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(productoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + productoRequestDTO.getUsuarioId()));

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
        producto.setUsuario(usuario);
        producto.setCategoria(categoria); // Asignar la categoría

        // Guardar la URL de la imagen
        if (productoRequestDTO.getImagenUrl() != null) {
            producto.setImagenUrl(productoRequestDTO.getImagenUrl());
        } else {
            // URL de imagen por defecto si no se proporciona una
            producto.setImagenUrl("https://via.placeholder.com/150");
        }

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

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarProductos(String query) {
        return productoRepository.findByNombreContainingIgnoreCase(query);
    }

    public Producto cambiarEstadoProducto(Long id, Boolean activo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setActivo(activo);
        return productoRepository.save(producto);
    }

    public List<Producto> obtenerProductosDelVendedor() {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            return productoRepository.findByUsuario(usuario);
        }

        return List.of();
    }
    
    /**
     * Obtiene una lista de productos destacados aleatorios.
     * Los productos se actualizan cada 10 minutos para mantener la frescura del contenido.
     * @return Lista de productos destacados aleatorios
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosDestacadosAleatorios() {
        long tiempoActual = System.currentTimeMillis();
        
        // Si el cache está vacío o ha pasado el tiempo de actualización, regeneramos la lista
        if (productosDestacadosCache == null || 
            tiempoActual - ultimaActualizacionCache > INTERVALO_ACTUALIZACION_CACHE) {
            
            // Obtener todos los productos
            List<Producto> todosLosProductos = productoRepository.findAll();
            
            // Mezclar la lista para obtener productos aleatorios
            java.util.Collections.shuffle(todosLosProductos);
            
            // Tomar los primeros 8 productos (o menos si no hay suficientes)
            int cantidadProductos = Math.min(8, todosLosProductos.size());
            productosDestacadosCache = todosLosProductos.subList(0, cantidadProductos);
            
            // Actualizar el tiempo de la última actualización
            ultimaActualizacionCache = tiempoActual;
        }
        
        return productosDestacadosCache;
    }
}