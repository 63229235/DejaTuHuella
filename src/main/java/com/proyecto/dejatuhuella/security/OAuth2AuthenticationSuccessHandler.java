package com.proyecto.dejatuhuella.security;

import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final UsuarioRepository usuarioRepository;

    public OAuth2AuthenticationSuccessHandler(UsuarioRepository usuarioRepository) {
        // Redirigir a la página principal después de la autenticación exitosa
        setDefaultTargetUrl("/");
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            
            logger.info("Usuario autenticado con OAuth2: {}", email);
            
            // Buscar el usuario en la base de datos
            usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                logger.info("Usuario encontrado en la base de datos: {}, Rol: {}", email, usuario.getRol());
                
                // Crear una nueva autenticación con CustomUserDetails para mantener consistencia
                try {
                    // Crear autoridades basadas en el rol del usuario
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    String roleName = "ROLE_" + usuario.getRol().name();
                    authorities.add(new SimpleGrantedAuthority(roleName));
                    
                    // Crear CustomUserDetails con el usuario
                    CustomUserDetails userDetails = new CustomUserDetails(
                            usuario.getEmail(),
                            usuario.getPassword(),
                            authorities,
                            usuario
                    );
                    
                    // Crear nueva autenticación
                    Authentication newAuth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
                    
                    // Establecer la nueva autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(newAuth);
                    
                    logger.info("Autenticación actualizada para usuario OAuth2: {}", email);
                } catch (Exception e) {
                    logger.error("Error al actualizar la autenticación para usuario OAuth2: {}", e.getMessage());
                }
            });
        }
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
}