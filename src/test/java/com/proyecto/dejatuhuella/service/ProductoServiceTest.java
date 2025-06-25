package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.ProductoRequestDTO;
import com.proyecto.dejatuhuella.model.Categoria;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.CategoriaRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    
    @Mock
    private UsuarioService usuarioService;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto1;
    private Producto producto2;
    private List<Producto> productoList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crear categoría de prueba
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Categoría de Prueba");

        // Crear productos de prueba
        producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Producto 1");
        producto1.setDescripcion("Descripción del producto 1");
        producto1.setPrecio(new BigDecimal("100.00"));
        producto1.setStock(10);
        producto1.setCategoria(categoria);
        producto1.setImagenUrl("imagen1.jpg");

        producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Producto 2");
        producto2.setDescripcion("Descripción del producto 2");
        producto2.setPrecio(new BigDecimal("200.00"));
        producto2.setStock(20);
        producto2.setCategoria(categoria);
        producto2.setImagenUrl("imagen2.jpg");

        productoList = Arrays.asList(producto1, producto2);
    }

    @Test
    void obtenerTodosLosProductos_deberiaRetornarListaDeProductos() {
        // Configurar mock
        when(productoRepository.findAll()).thenReturn(productoList);

        // Ejecutar
        List<Producto> resultado = productoService.obtenerTodosLosProductos();

        // Verificar
        assertEquals(2, resultado.size());
        assertEquals("Producto 1", resultado.get(0).getNombre());
        assertEquals("Producto 2", resultado.get(1).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerProductoPorId_deberiaRetornarProducto_cuandoExiste() {
        // Configurar mock
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));

        // Ejecutar
        Optional<Producto> resultado = productoService.obtenerProductoPorId(1L);

        // Verificar
        assertTrue(resultado.isPresent());
        assertEquals("Producto 1", resultado.get().getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerProductoPorId_deberiaRetornarEmpty_cuandoNoExiste() {
        // Configurar mock
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // Ejecutar
        Optional<Producto> resultado = productoService.obtenerProductoPorId(999L);

        // Verificar
        assertFalse(resultado.isPresent());
        verify(productoRepository, times(1)).findById(999L);
    }

    @Test
    void crearProducto_deberiaGuardarYRetornarProducto() {
        // Configurar mock
        when(productoRepository.save(any(Producto.class))).thenReturn(producto1);
        
        // Crear DTO para la prueba
        ProductoRequestDTO productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setNombre("Producto 1");
        productoRequestDTO.setDescripcion("Descripción del producto 1");
        productoRequestDTO.setPrecio(new BigDecimal("100.00"));
        productoRequestDTO.setStock(10);
        productoRequestDTO.setCategoriaId(1L);
        productoRequestDTO.setUsuarioId(1L);
        productoRequestDTO.setImagenUrl("imagen1.jpg");
        
        // Mock para usuario y categoría
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        
        // Configurar mocks adicionales
        UsuarioService usuarioService = mock(UsuarioService.class);
        CategoriaRepository categoriaRepository = mock(CategoriaRepository.class);
        
        // Inyectar mocks manualmente
        ReflectionTestUtils.setField(productoService, "usuarioService", usuarioService);
        ReflectionTestUtils.setField(productoService, "categoriaRepository", categoriaRepository);
        
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // Ejecutar
        Producto resultado = productoService.crearProducto(productoRequestDTO);

        // Verificar
        assertNotNull(resultado);
        assertEquals("Producto 1", resultado.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void eliminarProducto_deberiaEliminarProducto() {
        // Ejecutar
        productoService.eliminarProducto(1L);

        // Verificar
        verify(productoRepository, times(1)).deleteById(1L);
    }

    //@Test
   // void obtenerProductosPaginados_deberiaRetornarProductosPaginados() {
        // Configurar mock
        //Pageable pageable = PageRequest.of(0, 10);
        //Page<Producto> page = new PageImpl<>(productoList, pageable, productoList.size());
        //when(productoRepository.findAll(pageable)).thenReturn(page);

        // Ejecutar
        //Page<Producto> resultado = productoService.obtenerProductosPaginados(pageable);

        // Verificar
        //assertEquals(2, resultado.getContent().size());
       // assertEquals("Producto 1", resultado.getContent().get(0).getNombre());
        //assertEquals("Producto 2", resultado.getContent().get(1).getNombre());
        //verify(productoRepository, times(1)).findAll(pageable);
    //}

    @Test
    void buscarProductos_deberiaRetornarProductosQueCoinciden() {
        // Configurar mock
        when(productoRepository.findByNombreContainingIgnoreCase("Producto")).thenReturn(productoList);

        // Ejecutar
        List<Producto> resultado = productoService.buscarProductos("Producto");

        // Verificar
        assertEquals(2, resultado.size());
        assertEquals("Producto 1", resultado.get(0).getNombre());
        assertEquals("Producto 2", resultado.get(1).getNombre());
        verify(productoRepository, times(1)).findByNombreContainingIgnoreCase("Producto");
    }

    @Test
    void obtenerProductosPorCategoria_deberiaRetornarProductosDeCategoria() {
        // Configurar mock
        when(productoRepository.findByCategoriaId(1L)).thenReturn(productoList);

        // Ejecutar
        List<Producto> resultado = productoService.obtenerProductosPorCategoria(1L);

        // Verificar
        assertEquals(2, resultado.size());
        assertEquals("Producto 1", resultado.get(0).getNombre());
        assertEquals("Producto 2", resultado.get(1).getNombre());
        verify(productoRepository, times(1)).findByCategoriaId(1L);
    }
    
    @Test
    void obtenerProductosPorUsuario_deberiaRetornarProductosDelUsuario() {
        // Configurar mock
        when(productoRepository.findByUsuarioId(1L)).thenReturn(productoList);

        // Ejecutar
        List<Producto> resultado = productoService.obtenerProductosPorUsuario(1L);

        // Verificar
        assertEquals(2, resultado.size());
        assertEquals("Producto 1", resultado.get(0).getNombre());
        assertEquals("Producto 2", resultado.get(1).getNombre());
        verify(productoRepository, times(1)).findByUsuarioId(1L);
    }
    
    @Test
    void actualizarProducto_deberiaActualizarYRetornarProducto() {
        // Configurar mock
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto1);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(producto1.getCategoria()));
        
        // Crear DTO para la prueba
        ProductoRequestDTO productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setNombre("Producto 1 Actualizado");
        productoRequestDTO.setDescripcion("Descripción actualizada");
        productoRequestDTO.setPrecio(new BigDecimal("150.00"));
        productoRequestDTO.setStock(15);
        productoRequestDTO.setCategoriaId(1L);
        
        // Ejecutar
        Producto resultado = productoService.actualizarProducto(1L, productoRequestDTO);
        
        // Verificar
        assertNotNull(resultado);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
    
    @Test
    void cambiarEstadoProducto_deberiaActualizarEstadoYRetornarProducto() {
        // Configurar mock
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto1);
        
        // Ejecutar
        Producto resultado = productoService.cambiarEstadoProducto(1L, false);
        
        // Verificar
        assertNotNull(resultado);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
    
    @Test
    void obtenerProductosDelVendedor_deberiaRetornarProductosDelVendedorAutenticado() {
        // Este test es más complejo porque involucra SecurityContextHolder
        // Aquí se muestra una versión simplificada
        
        // Crear usuario autenticado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");
        
        // Configurar mocks
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserDetails userDetails = mock(UserDetails.class);
        
        // Configurar comportamiento de los mocks
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(productoRepository.findByUsuario(usuario)).thenReturn(productoList);
        
        // Establecer SecurityContext
        SecurityContextHolder.setContext(securityContext);
        
        // Ejecutar
        List<Producto> resultado = productoService.obtenerProductosDelVendedor();
        
        // Verificar
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findByUsuario(usuario);
        
        // Limpiar SecurityContext
        SecurityContextHolder.clearContext();
    }
}