package com.proyecto.dejatuhuella.security;

import com.proyecto.dejatuhuella.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final Usuario usuario;

    public CustomUserDetails(String username, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             Usuario usuario) {
        super(username, password, authorities);
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
