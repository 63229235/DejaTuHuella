package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Asegúrate de tener esta dependencia
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // Añadido para obtenerTodosLosUsuarios
import java.util.Optional; // Añadido para obtenerUsuarioPorId

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Necesario para encriptar contraseñas

    // ... existing code ...

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }


    @Transactional
    public Usuario actualizarUsuario(Usuario usuarioExistente, Usuario datosNuevosDelRequest) {
        // 'usuarioExistente' es la entidad cargada desde la BD (gestionada por JPA)
        // 'datosNuevosDelRequest' es el objeto que viene del @RequestBody en el controlador

        // Actualizar los campos de usuarioExistente con los valores de datosNuevosDelRequest
        if (datosNuevosDelRequest.getNombre() != null) {
            usuarioExistente.setNombre(datosNuevosDelRequest.getNombre());
        }
        if (datosNuevosDelRequest.getApellido() != null) {
            usuarioExistente.setApellido(datosNuevosDelRequest.getApellido());
        }

        // Lógica para el email:
        // Solo actualizar si el email es diferente y no está ya en uso por OTRO usuario.
        if (datosNuevosDelRequest.getEmail() != null && !datosNuevosDelRequest.getEmail().equals(usuarioExistente.getEmail())) {
            if (usuarioRepository.findByEmail(datosNuevosDelRequest.getEmail()) != null &&
                    !usuarioRepository.findByEmail(datosNuevosDelRequest.getEmail()).getId().equals(usuarioExistente.getId())) {
                throw new RuntimeException("El email '" + datosNuevosDelRequest.getEmail() + "' ya está en uso por otro usuario.");
            }
            usuarioExistente.setEmail(datosNuevosDelRequest.getEmail());
        }

        // Lógica para la contraseña:
        // Solo actualizar si se proporciona una nueva contraseña (no vacía)
        if (datosNuevosDelRequest.getPassword() != null && !datosNuevosDelRequest.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(datosNuevosDelRequest.getPassword()));
        }

        // Lógica para el rol:
        // Considera si este cambio debe ser permitido y bajo qué condiciones (ej. solo por un ADMIN)
        if (datosNuevosDelRequest.getRol() != null && datosNuevosDelRequest.getRol() != usuarioExistente.getRol()) {
            // Aquí podrías añadir validaciones de seguridad si es necesario.
            // Por ejemplo, verificar si el usuario autenticado tiene permisos para cambiar roles.
            usuarioExistente.setRol(datosNuevosDelRequest.getRol());
        }

        // Guarda la entidad usuarioExistente actualizada.
        // Como usuarioExistente es una entidad gestionada por JPA y estamos dentro de una transacción,
        // los cambios se persistirán al finalizar el método, pero un save explícito es una buena práctica.
        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

}
