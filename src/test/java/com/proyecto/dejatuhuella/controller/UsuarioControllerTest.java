package com.proyecto.dejatuhuella.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.dejatuhuella.dto.UsuarioDTO;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.service.SeguridadService;
import com.proyecto.dejatuhuella.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private SeguridadService seguridadService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        // Configurar un usuario de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@example.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USUARIO);
        usuario.setActivo(true);

        // Configurar un DTO de usuario de prueba
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombre("Juan Actualizado");
        usuarioDTO.setApellido("Pérez Actualizado");
        usuarioDTO.setTelefono("123456789");
        usuarioDTO.setDireccion("Calle Principal 123");
    }

    @Test
    @DisplayName("Debería crear un usuario correctamente")
    void crearUsuario() throws Exception {
        // Arrange
        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.apellido", is("Pérez")));
    }

    @Test
    @DisplayName("Debería manejar error al crear usuario con email existente")
    void crearUsuarioEmailExistente() throws Exception {
        // Arrange
        when(usuarioService.crearUsuario(any(Usuario.class)))
                .thenThrow(new RuntimeException("El email ya está registrado"));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Debería obtener todos los usuarios como administrador")
    void obtenerTodosLosUsuarios() throws Exception {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("María");
        usuario2.setApellido("López");

        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(Arrays.asList(usuario, usuario2));

        // Act & Assert
        mockMvc.perform(get("/api/usuarios")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    @DisplayName("No debería permitir obtener todos los usuarios como usuario normal")
    void obtenerTodosLosUsuariosComoUsuarioNormal() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Debería obtener un usuario por ID como administrador")
    void obtenerUsuarioPorIdComoAdmin() throws Exception {
        // Arrange
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")));
    }

    @Test
    @WithMockUser(username = "juan.perez@example.com", roles = "USUARIO")
    @DisplayName("Debería obtener su propio usuario como usuario normal")
    void obtenerUsuarioPropioComoUsuarioNormal() throws Exception {
        // Arrange
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario));
        when(seguridadService.esUsuarioActual(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")));
    }

    @Test
    @WithMockUser(username = "otro.usuario@example.com", roles = "USUARIO")
    @DisplayName("No debería permitir obtener otro usuario como usuario normal")
    void obtenerOtroUsuarioComoUsuarioNormal() throws Exception {
        // Arrange
        when(seguridadService.esUsuarioActual(1L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Debería actualizar un usuario como administrador")
    void actualizarUsuarioComoAdmin() throws Exception {
        // Arrange
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setNombre("Juan Actualizado");
        usuarioActualizado.setApellido("Pérez Actualizado");

        when(usuarioService.actualizarUsuario(anyLong(), any(Usuario.class)))
                .thenReturn(Optional.of(usuarioActualizado));

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan Actualizado")))
                .andExpect(jsonPath("$.apellido", is("Pérez Actualizado")));
    }

    @Test
    @WithMockUser(username = "juan.perez@example.com", roles = "USUARIO")
    @DisplayName("Debería actualizar su propio usuario como usuario normal")
    void actualizarUsuarioPropioComoUsuarioNormal() throws Exception {
        // Arrange
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setNombre("Juan Actualizado");
        usuarioActualizado.setApellido("Pérez Actualizado");

        when(seguridadService.esUsuarioActual(1L)).thenReturn(true);
        when(usuarioService.actualizarUsuario(anyLong(), any(Usuario.class)))
                .thenReturn(Optional.of(usuarioActualizado));

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan Actualizado")));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Debería eliminar un usuario como administrador")
    void eliminarUsuarioComoAdmin() throws Exception {
        // Arrange
        when(usuarioService.eliminarUsuario(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    @DisplayName("No debería permitir eliminar un usuario como usuario normal")
    void eliminarUsuarioComoUsuarioNormal() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "juan.perez@example.com", roles = "USUARIO")
    @DisplayName("Debería actualizar el perfil de usuario autenticado")
    void actualizarPerfil() throws Exception {
        // Arrange
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setNombre("Juan Actualizado");
        usuarioActualizado.setApellido("Pérez Actualizado");

        when(usuarioService.actualizarPerfilUsuario(anyString(), any(UsuarioDTO.class)))
                .thenReturn(usuarioActualizado);

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/perfil")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan Actualizado")))
                .andExpect(jsonPath("$.apellido", is("Pérez Actualizado")));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Debería cambiar el estado de un usuario como administrador")
    void cambiarEstadoUsuario() throws Exception {
        // Arrange
        Map<String, Boolean> estado = new HashMap<>();
        estado.put("activo", false);

        Usuario usuarioDesactivado = new Usuario();
        usuarioDesactivado.setId(1L);
        usuarioDesactivado.setNombre("Juan");
        usuarioDesactivado.setApellido("Pérez");
        usuarioDesactivado.setActivo(false);

        when(usuarioService.cambiarEstadoUsuario(anyLong(), any(Boolean.class)))
                .thenReturn(usuarioDesactivado);

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(false)));
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    @DisplayName("No debería permitir cambiar el estado de un usuario como usuario normal")
    void cambiarEstadoUsuarioComoUsuarioNormal() throws Exception {
        // Arrange
        Map<String, Boolean> estado = new HashMap<>();
        estado.put("activo", false);

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estado)))
                .andExpect(status().isForbidden());
    }
}