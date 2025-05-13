package com.dam.security;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {
	private static final String SECRET_KEY = "mi_clave_muy_segura_1234567890abcdefg"; // 256 bits o más;
    private static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos
    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256) // ✅ Forzamos HS256
                .compact();

        System.out.println("🟢 Token generado: " + token);
        return token;
    }


    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateToken(String token) {
        try {
            String algorithm = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getHeader()
                    .getAlgorithm();

            if (!algorithm.equals("HS256")) {
                System.out.println("❌ Token con algoritmo incorrecto: " + algorithm);
                return false;
            }

            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token);

            return true;
        } catch (JwtException e) {
            System.out.println("❌ Token inválido: " + e.getMessage());
            return false;
        }
    }

}

