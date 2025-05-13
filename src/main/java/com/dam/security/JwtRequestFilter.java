package com.dam.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dam.service.UsuarioService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final UsuarioService usuarioService;
    private final JwtTokenUtil jwtTokenUtil;
    public JwtRequestFilter(UsuarioService usuarioService, JwtTokenUtil jwtTokenUtil) {
        this.usuarioService = usuarioService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, java.io.IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ No se recibió token en la cabecera Authorization.");
            chain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        // Evitar errores si el token está vacío
        if (jwt.trim().isEmpty()) {
            System.out.println("⚠️ Token vacío, se ignora la autenticación.");
            chain.doFilter(request, response);
            return;
        }

        System.out.println("🔹 Token recibido en filtro: " + jwt);

        try {
            String username = jwtTokenUtil.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = usuarioService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(jwt)) {
                    System.out.println("✅ Token válido para usuario: " + username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("❌ Token inválido.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error al procesar el token: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }


}