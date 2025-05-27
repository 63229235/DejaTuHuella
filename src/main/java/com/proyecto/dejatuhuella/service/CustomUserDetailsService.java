package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Intentando cargar usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            logger.warn("Usuario no encontrado con email: {}", email);
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }

        logger.info("Usuario encontrado: {}, Rol: {}", usuario.getEmail(), usuario.getRol().name());
        // Por seguridad, NUNCA registres la contraseña, ni siquiera la codificada, en producción.
        // logger.debug("Contraseña codificada del usuario: {}", usuario.getPassword());

        Set<GrantedAuthority> authorities = new HashSet<>();
        String roleName = "ROLE_" + usuario.getRol().name();
        authorities.add(new SimpleGrantedAuthority(roleName));
        logger.info("Autoridades asignadas para {}: {}", email, roleName);

        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}