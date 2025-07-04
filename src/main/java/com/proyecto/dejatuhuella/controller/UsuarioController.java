package com.proyecto.dejatuhuella.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.dejatuhuella.dto.UsuarioDTO;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Considera restringir esto en producción
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint público para registrar nuevos usuarios
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        log.info("Intentando crear usuario con datos: {}", usuario);
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            log.info("Usuario creado exitosamente: {}", nuevoUsuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            log.error("Error al crear usuario (RuntimeException): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            log.error("Error inesperado al crear usuario: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Solo administradores pueden ver todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        log.info("Solicitud para obtener todos los usuarios.");
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @seguridadService.esUsuarioActual(#id)")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        log.info("Solicitud para obtener usuario por ID: {}", id);
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @seguridadService.esUsuarioActual(#id)")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        log.info("Intentando actualizar usuario ID: {} con datos: {}", id, usuario);
        return usuarioService.actualizarUsuario(id, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("Intentando eliminar usuario ID: {}", id);
        if (usuarioService.eliminarUsuario(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> actualizarPerfil(@RequestBody UsuarioDTO usuarioDTO) {
        log.info("Intentando actualizar perfil con datos: {}", usuarioDTO);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName(); // Obtiene el email del usuario autenticado
            Usuario usuarioActualizado = usuarioService.actualizarPerfilUsuario(email, usuarioDTO);
            log.info("Perfil actualizado exitosamente para: {}", email);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            log.error("Error al actualizar perfil: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Usuario> cambiarEstadoUsuario(@PathVariable Long id, @RequestBody Map<String, Boolean> estado) {
        log.info("Intentando cambiar estado del usuario ID: {} a: {}", id, estado.get("activo"));
        try {
            Usuario usuario = usuarioService.cambiarEstadoUsuario(id, estado.get("activo"));
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            log.error("Error al cambiar estado del usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
