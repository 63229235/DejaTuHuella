package com.proyecto.dejatuhuella.config;

// Ya no se necesita @Autowired aquí si no se usa directamente
// import org.springframework.beans.factory.annotation.Autowired;
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

// No es necesario importar CustomUserDetailsService aquí si no se usa directamente en esta clase.
// Spring lo encontrará automáticamente si es un @Service y lo usará para el AuthenticationManager.
// import com.proyecto.dejatuhuella.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // El campo CustomUserDetailsService ya no es necesario aquí,
    // Spring Security lo detectará automáticamente si es un bean @Service.
    /*
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                                .requestMatchers("/", "/home", "/registro", "/login", "/css/**", "/static/js/**", "/images/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Permite el registro público
                                .requestMatchers("/api/public/**", "/api/categorias/**", "/api/productos/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                                .requestMatchers("/api/usuario/**").hasAnyRole("USUARIO", "ADMINISTRADOR")
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .permitAll()
                )
                .logout(logout ->
                        logout.permitAll()
                );
        return http.build();
    }

    // El método configureGlobal ya no es la forma preferida de configurar UserDetailsService
    // con las versiones más recientes de Spring Security cuando se usa Java config.
    // Spring Boot lo configurará automáticamente si CustomUserDetailsService es un bean @Service.
    /*
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }
    */
}