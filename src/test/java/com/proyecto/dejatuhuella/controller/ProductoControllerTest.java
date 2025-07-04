package com.proyecto.dejatuhuella.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.dejatuhuella.model.Categoria;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.service.ProductoService;
import com.proyecto.dejatuhuella.service.SeguridadService;
import com.proyecto.dejatuhuella.service.FileStorageService;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import(TestSecurityConfig.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;
    
    @MockBean
    private SeguridadService seguridadService;
    
    @MockBean
    private FileStorageService fileStorageService;
    
    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;
    private Categoria categoria;
    private Producto producto;
    private List<Producto> productos;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail("test@test.com");
        usuario.setRol(Rol.USUARIO);

        // Crear categoría de prueba
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónicos");
        categoria.setDescripcion("Productos electrónicos");

        // Crear producto de prueba
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setDescripcion("Laptop de alta gama");
        producto.setPrecio(new BigDecimal("1500.00"));
        producto.setStock(10);
        producto.setActivo(true);
        producto.setUsuario(usuario);
        producto.setCategoria(categoria);

        productos = Arrays.asList(producto);
    }

    @Test
    void obtenerTodosLosProductos_DeberiaRetornarListaDeProductos() throws Exception {
        when(productoService.obtenerTodosLosProductos()).thenReturn(productos);

        mockMvc.perform(get("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Laptop"))
                .andExpect(jsonPath("$[0].precio").value(1500.00));
    }

    @Test
    void obtenerProductoPorId_CuandoExiste_DeberiaRetornarProducto() throws Exception {
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.precio").value(1500.00));
    }

    @Test
    void obtenerProductoPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(productoService.obtenerProductoPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerProductosPorCategoria_DeberiaRetornarProductosDeLaCategoria() throws Exception {
        when(productoService.obtenerProductosPorCategoria(1L)).thenReturn(productos);

        mockMvc.perform(get("/api/productos/categoria/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].categoria.id").value(1L));
    }

    @Test
    void buscarProductos_DeberiaRetornarProductosQueCoincidan() throws Exception {
        when(productoService.buscarProductos("Laptop")).thenReturn(productos);

        mockMvc.perform(get("/api/productos/buscar")
                        .param("query", "Laptop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laptop"));
    }
}