package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import com.proyecto.dejatuhuella.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UsuarioRepository usuarioRepository;

    // Agregar una propiedad para almacenar el usuario actual
    private Usuario usuario;
    
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Intentando cargar usuario por email: {}", email);
        usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        logger.info("Usuario encontrado: {}, Rol: {}", usuario.getEmail(), usuario.getRol().name());

        Set<GrantedAuthority> authorities = new HashSet<>();
        String roleName = "ROLE_" + usuario.getRol().name();
        authorities.add(new SimpleGrantedAuthority(roleName));
        logger.info("Autoridades asignadas para {}: {}", email, roleName);

        return new CustomUserDetails(usuario.getEmail(), usuario.getPassword(), authorities, usuario);
    }

    // Método para obtener el usuario actual
    public Usuario getUsuario() {
        return usuario;
    }
}