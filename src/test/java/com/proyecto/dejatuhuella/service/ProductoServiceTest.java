package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.ProductoRequestDTO;
import com.proyecto.dejatuhuella.model.Categoria;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.repository.CategoriaRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ProductoService productoService;

    private Usuario usuario;
    private Categoria categoria;
    private Producto producto;
    private ProductoRequestDTO productoRequestDTO;

    @BeforeEach
    void setUp() {
        // Configurar un usuario de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@example.com");
        usuario.setRol(Rol.USUARIO);

        // Configurar una categoría de prueba
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Mascotas");

        // Configurar un producto de prueba
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Collar para perro");
        producto.setDescripcion("Collar ajustable para perros de todos los tamaños");
        producto.setPrecio(new BigDecimal("19.99"));
        producto.setStock(50);
        producto.setUsuario(usuario);
        producto.setCategoria(categoria);
        producto.setActivo(true);
        producto.setImagenUrl("https://example.com/collar.jpg");

        // Configurar un DTO de producto de prueba
        productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setNombre("Collar para perro");
        productoRequestDTO.setDescripcion("Collar ajustable para perros de todos los tamaños");
        productoRequestDTO.setPrecio(new BigDecimal("19.99"));
        productoRequestDTO.setStock(50);
        productoRequestDTO.setUsuarioId(1L);
        productoRequestDTO.setCategoriaId(1L);
        productoRequestDTO.setImagenUrl("https://example.com/collar.jpg");
    }

    @Test
    @DisplayName("Debería obtener todos los productos")
    void obtenerTodosLosProductos() {
        // Arrange
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Juguete para gato");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto, producto2));

        // Act
        List<Producto> productos = productoService.obtenerTodosLosProductos();

        // Assert
        assertEquals(2, productos.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un producto por ID")
    void obtenerProductoPorId() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        Optional<Producto> resultado = productoService.obtenerProductoPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(producto.getId(), resultado.get().getId());
        assertEquals(producto.getNombre(), resultado.get().getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería obtener productos por usuario")
    void obtenerProductosPorUsuario() {
        // Arrange
        when(productoRepository.findByUsuarioId(1L)).thenReturn(List.of(producto));

        // Act
        List<Producto> productos = productoService.obtenerProductosPorUsuario(1L);

        // Assert
        assertEquals(1, productos.size());
        assertEquals(producto.getId(), productos.get(0).getId());
        verify(productoRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("Debería crear un producto correctamente")
    void crearProducto() {
        // Arrange
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        Producto resultado = productoService.crearProducto(productoRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(producto.getId(), resultado.getId());
        assertEquals(producto.getNombre(), resultado.getNombre());
        assertEquals(producto.getCategoria().getId(), resultado.getCategoria().getId());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción al crear producto con usuario no existente")
    void crearProductoUsuarioNoExistente() {
        // Arrange
        when(usuarioService.obtenerUsuarioPorId(99L)).thenReturn(Optional.empty());
        productoRequestDTO.setUsuarioId(99L);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            productoService.crearProducto(productoRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería actualizar un producto correctamente")
    void actualizarProducto() {
        // Arrange
        ProductoRequestDTO actualizacionDTO = new ProductoRequestDTO();
        actualizacionDTO.setNombre("Collar para perro actualizado");
        actualizacionDTO.setPrecio(new BigDecimal("24.99"));
        actualizacionDTO.setCategoriaId(1L);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        Producto resultado = productoService.actualizarProducto(1L, actualizacionDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Collar para perro actualizado", resultado.getNombre());
        assertEquals(new BigDecimal("24.99"), resultado.getPrecio());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar producto no existente")
    void actualizarProductoNoExistente() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            productoService.actualizarProducto(99L, productoRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado"));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería eliminar un producto correctamente")
    void eliminarProducto() {
        // Arrange
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        // Act & Assert
        assertDoesNotThrow(() -> productoService.eliminarProducto(1L));
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar producto no existente")
    void eliminarProductoNoExistente() {
        // Arrange
        when(productoRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            productoService.eliminarProducto(99L);
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado"));
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debería obtener productos por categoría")
    void obtenerProductosPorCategoria() {
        // Arrange
        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(producto));

        // Act
        List<Producto> productos = productoService.obtenerProductosPorCategoria(1L);

        // Assert
        assertEquals(1, productos.size());
        assertEquals(producto.getId(), productos.get(0).getId());
        verify(productoRepository, times(1)).findByCategoriaId(1L);
    }

    @Test
    @DisplayName("Debería buscar productos por nombre")
    void buscarProductos() {
        // Arrange
        when(productoRepository.findByNombreContainingIgnoreCase("collar")).thenReturn(List.of(producto));

        // Act
        List<Producto> productos = productoService.buscarProductos("collar");

        // Assert
        assertEquals(1, productos.size());
        assertEquals(producto.getId(), productos.get(0).getId());
        verify(productoRepository, times(1)).findByNombreContainingIgnoreCase("collar");
    }

    @Test
    @DisplayName("Debería cambiar el estado de un producto")
    void cambiarEstadoProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        Producto resultado = productoService.cambiarEstadoProducto(1L, false);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería obtener productos del vendedor autenticado")
    void obtenerProductosDelVendedor() {
        // Arrange - Configurar el contexto de seguridad simulado
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("juan.perez@example.com");
        when(usuarioRepository.findByEmail("juan.perez@example.com")).thenReturn(Optional.of(usuario));
        when(productoRepository.findByUsuario(usuario)).thenReturn(List.of(producto));

        // Act
        List<Producto> productos = productoService.obtenerProductosDelVendedor();

        // Assert
        assertEquals(1, productos.size());
        assertEquals(producto.getId(), productos.get(0).getId());
        verify(productoRepository, times(1)).findByUsuario(usuario);

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Debería obtener productos destacados aleatorios")
    void obtenerProductosDestacadosAleatorios() {
        // Arrange
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Juguete para gato");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto, producto2));

        // Act
        List<Producto> productos = productoService.obtenerProductosDestacadosAleatorios();

        // Assert
        assertEquals(2, productos.size());
        verify(productoRepository, times(1)).findAll();

        // Segunda llamada debería usar el caché
        productoService.obtenerProductosDestacadosAleatorios();
        // Verificar que findAll() solo se llamó una vez (la primera vez)
        verify(productoRepository, times(1)).findAll();
    }
}