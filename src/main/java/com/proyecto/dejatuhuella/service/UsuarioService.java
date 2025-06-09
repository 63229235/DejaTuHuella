package com.proyecto.dejatuhuella.service;

import java.util.List;
import java.util.Optional;

import com.proyecto.dejatuhuella.dto.UsuarioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Eliminar esta inyección incorrecta
    // @Autowired
    // private UsuarioDTO usuarioDTO;

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        log.info("Servicio: Intentando crear usuario: {}", usuario);
        // Solo asignar rol por defecto si es null
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USUARIO);
            log.info("Servicio: Rol asignado por defecto: USUARIO para el usuario: {}", usuario.getEmail());
        } else {
            log.info("Servicio: Usando rol seleccionado: {} para el usuario: {}", usuario.getRol(), usuario.getEmail());
        }

        // Verificar si el email ya existe antes de intentar guardar
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            log.warn("Servicio: Email '{}' ya registrado.", usuario.getEmail());
            throw new RuntimeException("El email '" + usuario.getEmail() + "' ya está registrado.");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        log.info("Servicio: Contraseña codificada para el usuario: {}", usuario.getEmail());
        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Servicio: Usuario guardado en BD: {}", guardado);
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        log.info("Servicio: Obteniendo todos los usuarios.");
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        log.info("Servicio: Obteniendo usuario por ID: {}", id);
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario actualizarPerfilUsuario(String email, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar solo los campos permitidos
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setDireccion(usuarioDTO.getDireccion());

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        log.info("Servicio: Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Optional<Usuario> actualizarUsuario(Long id, Usuario usuarioDetalles) {
        log.info("Servicio: Intentando actualizar usuario ID: {} con detalles: {}", id, usuarioDetalles);
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNombre(usuarioDetalles.getNombre());
                    usuarioExistente.setApellido(usuarioDetalles.getApellido());
                    usuarioExistente.setEmail(usuarioDetalles.getEmail());
                    if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().isEmpty()) {
                        usuarioExistente.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
                        log.info("Servicio: Contraseña actualizada para usuario ID: {}", id);
                    }
                    // Considera si el rol puede ser actualizado y por quién
                    // usuarioExistente.setRol(usuarioDetalles.getRol());
                    Usuario actualizado = usuarioRepository.save(usuarioExistente);
                    log.info("Servicio: Usuario ID: {} actualizado: {}", id, actualizado);
                    return actualizado;
                });
    }

    @Transactional
    public boolean eliminarUsuario(Long id) {
        log.info("Servicio: Intentando eliminar usuario ID: {}", id);
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            log.info("Servicio: Usuario ID: {} eliminado.", id);
            return true;
        }
        log.warn("Servicio: Usuario ID: {} no encontrado para eliminar.", id);
        return false;
    }

    @Transactional
    public Usuario cambiarEstadoUsuario(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }
}