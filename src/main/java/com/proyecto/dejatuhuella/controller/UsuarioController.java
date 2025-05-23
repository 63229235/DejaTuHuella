package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.model.Usuario;
// Asegúrate de que Rol se importe correctamente si no está en el mismo paquete que Usuario
// import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.service.UsuarioService;
import com.proyecto.dejatuhuella.service.SeguridadService; // Necesario para @seguridadService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Importar List

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // SeguridadService para comprobaciones personalizadas como esUsuarioActual
    // Spring lo inyectará si está anotado con @Service y es un bean gestionado.
    // No es necesario un @Autowired aquí si se usa en la expresión SpEL directamente.

    // Solo administradores pueden ver todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() { // Especificar el tipo de respuesta
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    // Un usuario puede ver su propia información, o un administrador puede ver cualquier usuario
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @seguridadService.esUsuarioActual(#id)")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) { // Especificar el tipo de respuesta
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo administradores pueden crear nuevos usuarios (ej. desde un panel de admin)
    // Si necesitas autorregistro, se haría en un endpoint público, posiblemente en un AuthController.
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) { // Especificar el tipo de respuesta
        return ResponseEntity.ok(usuarioService.crearUsuario(usuario));
    }

    // Un usuario puede actualizar su propia información, o un administrador puede actualizar cualquier usuario
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @seguridadService.esUsuarioActual(#id)")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) { // Especificar el tipo de respuesta
        // Asegurarse de que el ID del path se usa para la entidad a actualizar
        // y que el usuario que actualiza no pueda cambiar su rol o el ID de otro.
        // Esta lógica de validación adicional iría en el UsuarioService.
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuarioExistente -> {
                    usuario.setId(id); // Asegurar que el ID es el del path
                    // Considerar no permitir cambiar el email o rol directamente aquí sin lógica de negocio adicional
                    return ResponseEntity.ok(usuarioService.actualizarUsuario(usuarioExistente, usuario)); // Pasar usuarioExistente y los datos nuevos
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo administradores pueden eliminar usuarios
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuarioExistente -> { // Cambiado 'usuario' a 'usuarioExistente' para claridad
                    usuarioService.eliminarUsuario(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}