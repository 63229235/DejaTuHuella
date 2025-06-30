package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.UsuarioDTO;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

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
    void crearUsuario() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.crearUsuario(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuario.getId(), resultado.getId());
        assertEquals(usuario.getNombre(), resultado.getNombre());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el email ya existe")
    void crearUsuarioEmailExistente() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(usuario);
        });

        assertTrue(exception.getMessage().contains("ya está registrado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debería obtener todos los usuarios")
    void obtenerTodosLosUsuarios() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("María");
        usuario2.setApellido("López");
        usuario2.setEmail("maria.lopez@example.com");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        // Act
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();

        // Assert
        assertEquals(2, usuarios.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un usuario por ID")
    void obtenerUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuario.getId(), resultado.get().getId());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería actualizar el perfil de un usuario")
    void actualizarPerfilUsuario() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.actualizarPerfilUsuario("juan.perez@example.com", usuarioDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioDTO.getNombre(), resultado.getNombre());
        assertEquals(usuarioDTO.getApellido(), resultado.getApellido());
        assertEquals(usuarioDTO.getTelefono(), resultado.getTelefono());
        assertEquals(usuarioDTO.getDireccion(), resultado.getDireccion());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar perfil de usuario no existente")
    void actualizarPerfilUsuarioNoExistente() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarPerfilUsuario("no.existe@example.com", usuarioDTO);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debería buscar un usuario por email")
    void buscarPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmail("juan.perez@example.com")).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorEmail("juan.perez@example.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("juan.perez@example.com", resultado.get().getEmail());
        verify(usuarioRepository, times(1)).findByEmail("juan.perez@example.com");
    }

    @Test
    @DisplayName("Debería actualizar un usuario")
    void actualizarUsuario() {
        // Arrange
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setNombre("Juan Actualizado");
        usuarioActualizado.setApellido("Pérez Actualizado");
        usuarioActualizado.setEmail("juan.actualizado@example.com");
        usuarioActualizado.setPassword("newPassword");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        // Act
        Optional<Usuario> resultado = usuarioService.actualizarUsuario(1L, usuarioActualizado);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Juan Actualizado", resultado.get().getNombre());
        assertEquals("Pérez Actualizado", resultado.get().getApellido());
        assertEquals("juan.actualizado@example.com", resultado.get().getEmail());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debería eliminar un usuario")
    void eliminarUsuario() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        // Act
        boolean resultado = usuarioService.eliminarUsuario(1L);

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debería retornar false al intentar eliminar un usuario que no existe")
    void eliminarUsuarioNoExistente() {
        // Arrange
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // Act
        boolean resultado = usuarioService.eliminarUsuario(99L);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository, never()).deleteById(99L);
    }

    @Test
    @DisplayName("Debería cambiar el estado de un usuario")
    void cambiarEstadoUsuario() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.cambiarEstadoUsuario(1L, false);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción al cambiar estado de usuario no existente")
    void cambiarEstadoUsuarioNoExistente() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.cambiarEstadoUsuario(99L, false);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}