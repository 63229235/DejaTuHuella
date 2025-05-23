package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.Rol;
import com.proyecto.dejatuhuella.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Solo administradores pueden ver todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    // Un usuario puede ver su propia información
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @seguridadService.esUsuarioActual(#id)")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo administradores pueden crear nuevos usuarios
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.crearUsuario(usuario));
    }

    // Un usuario puede actualizar su propia información, o un administrador puede actualizar cualquier usuario
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @seguridadService.esUsuarioActual(#id)")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuarioExistente -> {
                    usuario.setId(id);
                    return ResponseEntity.ok(usuarioService.actualizarUsuario(usuario));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo administradores pueden eliminar usuarios
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuario -> {
                    usuarioService.eliminarUsuario(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}