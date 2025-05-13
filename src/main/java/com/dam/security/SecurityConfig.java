package com.dam.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.dam.service.UsuarioService;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final UsuarioService usuarioService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UsuarioService usuarioService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.usuarioService = usuarioService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/usuarios/login", "/api/usuarios/register").permitAll() // Permitir login y registro
                .requestMatchers("/api/usuarios/me").authenticated() // Solo autenticados pueden acceder
                .requestMatchers("/api/bares/**").hasAnyRole("USER", "ADMIN") // Permitir a USER y ADMIN
                .requestMatchers("/api/bares/{barId}/menu").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/bares/**").hasAnyRole("USER", "ADMIN") // Lectura permitida
                .requestMatchers(HttpMethod.PUT, "/api/bares/**").hasAnyRole("ADMIN", "OWNER") 
                .requestMatchers(HttpMethod.POST, "/api/bares/**").hasRole("ADMIN") // Creación solo para ADMIN
                .requestMatchers(HttpMethod.DELETE, "/api/bares/**").hasRole("ADMIN") // Eliminación solo para ADMIN
                .requestMatchers("/api/desayunos/**").hasRole("ADMIN")
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/api/pedidos/**").hasAnyRole("USER", "ADMIN") // Pedidos accesibles a USER y ADMIN
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 🔥 NUEVA CONFIGURACIÓN PARA PERMITIR CORS 🔥
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000")); // Permite el frontend
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}
