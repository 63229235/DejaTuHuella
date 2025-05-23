package com.proyecto.dejatuhuella.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Importar BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // ... existing code ...

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para simplificar (considerar habilitar en producción)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/public/**", "/api/usuarios/registro", "/api/categorias/**", "/api/productos/**").permitAll() // Rutas públicas
                                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                                .requestMatchers("/api/vendedor/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                                .requestMatchers("/api/comprador/**").hasAnyRole("COMPRADOR", "ADMINISTRADOR")
                                .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                )
                .httpBasic(withDefaults()); // Habilitar autenticación básica

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}