package com.proyecto.dejatuhuella.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.service.UsuarioService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(TestSecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;
    
    @MockBean
    private SeguridadService seguridadService;
    
    @MockBean
    private FileStorageService fileStorageService;
    
    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;
    private Usuario administrador;
    private List<Usuario> usuarios;

    @BeforeEach
    void setUp() {
        // Crear usuario normal de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan@test.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USUARIO);
        usuario.setActivo(true);

        // Crear administrador de prueba
        administrador = new Usuario();
        administrador.setId(2L);
        administrador.setNombre("Admin");
        administrador.setApellido("Sistema");
        administrador.setEmail("admin@test.com");
        administrador.setPassword("admin123");
        administrador.setRol(Rol.ADMINISTRADOR);
        administrador.setActivo(true);

        usuarios = Arrays.asList(usuario, administrador);
    }

    @Test
    void crearUsuario_DeberiaCrearUsuarioExitosamente() throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Nuevo");
        nuevoUsuario.setApellido("Usuario");
        nuevoUsuario.setEmail("nuevo@test.com");
        nuevoUsuario.setPassword("password123");
        nuevoUsuario.setRol(Rol.USUARIO);

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMINISTRADOR"})
    void obtenerTodosLosUsuarios_ComoAdministrador_DeberiaRetornarListaDeUsuarios() throws Exception {
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMINISTRADOR"})
    void obtenerUsuarioPorId_ComoAdministrador_DeberiaRetornarUsuario() throws Exception {
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMINISTRADOR"})
    void obtenerUsuarioPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(usuarioService.obtenerUsuarioPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMINISTRADOR"})
    void actualizarUsuario_ComoAdministrador_DeberiaActualizarUsuario() throws Exception {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setNombre("Juan Actualizado");
        usuarioActualizado.setApellido("Pérez");
        usuarioActualizado.setEmail("juan@test.com");
        usuarioActualizado.setRol(Rol.USUARIO);

        when(usuarioService.actualizarUsuario(any(Long.class), any(Usuario.class)))
                .thenReturn(Optional.of(usuarioActualizado));

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"));
    }
}