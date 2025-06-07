package com.proyecto.dejatuhuella.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}