package com.proyecto.dejatuhuella.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.proyecto.dejatuhuella.service.CustomUserDetailsService; // Asegúrate de tener este import

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // CustomUserDetailsService será inyectado automáticamente por Spring donde sea necesario,
    // como en el AuthenticationManager que se configurará a continuación.
    // No es estrictamente necesario tener el @Autowired aquí si no se usa directamente en esta clase,
    // pero no hace daño.
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring Security usará este AuthenticationManager.
    // Se configura automáticamente con el UserDetailsService y PasswordEncoder disponibles.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/home", "/registro", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                                // ESTA LÍNEA HACE PÚBLICO EL REGISTRO (POST /api/usuarios)
                                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() 
                                .requestMatchers("/api/public/**", "/api/categorias/**", "/api/productos/**").permitAll() // Otros endpoints públicos
                                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                                .requestMatchers("/api/vendedor/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                                .requestMatchers("/api/comprador/**").hasAnyRole("COMPRADOR", "ADMINISTRADOR")
                                .anyRequest().authenticated() // Todas las demás solicitudes requieren autenticación
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login") // Especifica la página de login personalizada
                                .permitAll() // Permite a todos acceder a la página de login
                )
                .logout(logout ->
                        logout.permitAll() // Permite a todos acceder a la funcionalidad de logout
                );
        return http.build();
    }
}

    // Eliminamos el método configureGlobal(AuthenticationManagerBuilder auth)
    /*
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }
    */