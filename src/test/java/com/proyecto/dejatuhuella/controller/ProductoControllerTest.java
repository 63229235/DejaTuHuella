package com.proyecto.dejatuhuella.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.dejatuhuella.dto.ProductoRequestDTO;
import com.proyecto.dejatuhuella.model.Categoria;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import com.proyecto.dejatuhuella.service.FileStorageService;
import com.proyecto.dejatuhuella.service.ProductoService;
import com.proyecto.dejatuhuella.service.SeguridadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private SeguridadService seguridadService;

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
        producto.setImagenUrl("/uploads/collar.jpg");

        // Configurar un DTO de producto de prueba
        productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setNombre("Collar para perro");
        productoRequestDTO.setDescripcion("Collar ajustable para perros de todos los tamaños");
        productoRequestDTO.setPrecio(new BigDecimal("19.99"));
        productoRequestDTO.setStock(50);
        productoRequestDTO.setUsuarioId(1L);
        productoRequestDTO.setCategoriaId(1L);
        productoRequestDTO.setImagenUrl("/uploads/collar.jpg");
    }

    @Test
    @DisplayName("Debería obtener todos los productos")
    void obtenerTodosLosProductos() throws Exception {
        // Arrange
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Juguete para gato");

        when(productoService.obtenerTodosLosProductos()).thenReturn(Arrays.asList(producto, producto2));

        // Act & Assert
        mockMvc.perform(get("/api/productos")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("Debería obtener un producto por ID")
    void obtenerProductoPorId() throws Exception {
        // Arrange
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        mockMvc.perform(get("/api/productos/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Collar para perro")));
    }

    @Test
    @DisplayName("Debería retornar 404 cuando el producto no existe")
    void obtenerProductoPorIdNoExistente() throws Exception {
        // Arrange
        when(productoService.obtenerProductoPorId(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/productos/99")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "juan.perez@example.com", roles = "USUARIO")
    @DisplayName("Debería crear un producto correctamente")
    void crearProducto() throws Exception {
        // Arrange
        MockMultipartFile imagenFile = new MockMultipartFile(
                "imagen",
                "collar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes());

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(
                new org.springframework.security.core.userdetails.User("juan.perez@example.com", "", Arrays.asList()));
        when(usuarioRepository.findByEmail("juan.perez@example.com")).thenReturn(Optional.of(usuario));
        when(fileStorageService.storeFile(any())).thenReturn("/uploads/collar.jpg");
        when(productoService.crearProducto(any(ProductoRequestDTO.class))).thenReturn(producto);

        // Act & Assert
        mockMvc.perform(multipart("/api/productos")
                .file(imagenFile)
                .param("nombre", "Collar para perro")
                .param("descripcion", "Collar ajustable para perros de todos los tamaños")
                .param("precio", "19.99")
                .param("stock", "50")
                .param("categoriaId", "1")
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Collar para perro")));
    }

    @Test
    @WithMockUser(username = "juan.perez@example.com", roles = "USUARIO")
    @DisplayName("Debería actualizar un producto correctamente cuando es propietario")
    void actualizarProductoComoPropietario() throws Exception {
        // Arrange
        MockMultipartFile imagenFile = new MockMultipartFile(
                "imagen",
                "collar_updated.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "updated test image content".getBytes());

        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));
        when(seguridadService.esPropietarioDelProducto(1L)).thenReturn(true);
        when(fileStorageService.storeFile(any())).thenReturn("/uploads/collar_updated.jpg");
        when(productoService.actualizarProducto(anyLong(), any(ProductoRequestDTO.class))).thenReturn(producto);

        // Act & Assert
        mockMvc.perform(multipart("/api/productos/1")
                .file(imagenFile)
                .param("nombre", "Collar para perro actualizado")
                .param("descripcion", "Collar ajustable actualizado")
                .param("precio", "24.99")
                .param("stock", "40")
                .param("categoriaId", "1")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @WithMockUser(username = "otro.usuario@example.com", roles = "USUARIO")
    @DisplayName("No debería permitir actualizar un producto cuando no es propietario")
    void actualizarProductoSinSerPropietario() throws Exception {
        // Arrange
        when(seguridadService.esPropietarioDelProducto(1L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(multipart("/api/productos/1")
                .param("nombre", "Collar para perro actualizado")
                .param("descripcion", "Collar ajustable actualizado")
                .param("precio", "24.99")
                .param("stock", "40")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "juan.perez@example.com", roles = "USUARIO")
    @DisplayName("Debería eliminar un producto correctamente cuando es propietario")
    void eliminarProductoComoPropietario() throws Exception {
        // Arrange
        when(seguridadService.esPropietarioDelProducto(1L)).thenReturn(true);
        doNothing().when(productoService).eliminarProducto(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/productos/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "otro.usuario@example.com", roles = "USUARIO")
    @DisplayName("No debería permitir eliminar un producto cuando no es propietario")
    void eliminarProductoSinSerPropietario() throws Exception {
        // Arrange
        when(seguridadService.esPropietarioDelProducto(1L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/productos/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Debería cambiar el estado de un producto como administrador")
    void cambiarEstadoProductoComoAdmin() throws Exception {
        // Arrange
        Map<String, Boolean> estado = new HashMap<>();
        estado.put("activo", false);

        Producto productoDesactivado = new Producto();
        productoDesactivado.setId(1L);
        productoDesactivado.setNombre("Collar para perro");
        productoDesactivado.setActivo(false);

        when(productoService.cambiarEstadoProducto(anyLong(), any(Boolean.class)))
                .thenReturn(productoDesactivado);

        // Act & Assert
        mockMvc.perform(put("/api/productos/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(false)));
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    @DisplayName("No debería permitir cambiar el estado de un producto como usuario normal")
    void cambiarEstadoProductoComoUsuarioNormal() throws Exception {
        // Arrange
        Map<String, Boolean> estado = new HashMap<>();
        estado.put("activo", false);

        // Act & Assert
        mockMvc.perform(put("/api/productos/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estado)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Debería obtener productos por categoría")
    void obtenerProductosPorCategoria() throws Exception {
        // Arrange
        when(productoService.obtenerProductosPorCategoria(1L)).thenReturn(List.of(producto));

        // Act & Assert
        mockMvc.perform(get("/api/productos/categoria/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @DisplayName("Debería buscar productos por nombre")
    void buscarProductos() throws Exception {
        // Arrange
        when(productoService.buscarProductos("collar")).thenReturn(List.of(producto));

        // Act & Assert
        mockMvc.perform(get("/api/productos/buscar")
                .param("query", "collar")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}