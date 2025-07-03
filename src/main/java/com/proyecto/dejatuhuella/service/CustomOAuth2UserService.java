package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CustomOAuth2UserService(UsuarioRepository usuarioRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extraer información del usuario de Google
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String sub = (String) attributes.get("sub"); // ID único de Google

        logger.info("Usuario OAuth2 cargado: {}", email);

        // Buscar si el usuario ya existe en la base de datos
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        Usuario usuario;

        if (usuarioOptional.isPresent()) {
            // Usuario existente
            usuario = usuarioOptional.get();
            logger.info("Usuario existente encontrado: {}", email);
            
            // Actualizar información del proveedor si es necesario
            if (usuario.getProviderId() == null || !usuario.getProviderId().equals("google")) {
                usuario.setProviderId("google");
                usuario.setProviderUserId(sub);
            }
            
            // Actualizar URL de imagen si ha cambiado
            if (picture != null && (usuario.getImageUrl() == null || !usuario.getImageUrl().equals(picture))) {
                usuario.setImageUrl(picture);
            }
            
            usuarioRepository.save(usuario);
        } else {
            // Crear nuevo usuario
            logger.info("Creando nuevo usuario para: {}", email);
            
            // Dividir el nombre completo en nombre y apellido
            String[] nameParts = name.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            // Generar una contraseña aleatoria (el usuario nunca la usará directamente)
            String randomPassword = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(randomPassword);
            
            usuario = new Usuario();
            usuario.setNombre(firstName);
            usuario.setApellido(lastName);
            usuario.setEmail(email);
            usuario.setPassword(encodedPassword);
            usuario.setRol(Rol.USUARIO); // Rol por defecto
            usuario.setActivo(true);
            usuario.setProviderId("google");
            usuario.setProviderUserId(sub);
            usuario.setImageUrl(picture);
            
            usuarioRepository.save(usuario);
            logger.info("Nuevo usuario creado: {}", email);
        }

        // Crear autoridades basadas en el rol del usuario
        String roleName = "ROLE_" + usuario.getRol().name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        // Devolver un OAuth2User con las autoridades correctas
        return new DefaultOAuth2User(
                Collections.singleton(authority),
                attributes,
                "email");
    }
}