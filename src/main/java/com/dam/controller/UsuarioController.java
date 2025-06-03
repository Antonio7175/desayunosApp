package com.dam.controller;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.dam.dto.CambiarPasswordRequest;
import com.dam.dto.LoginRequest;
import com.dam.dto.LoginResponse;
import com.dam.entity.Usuario;
import com.dam.service.UsuarioService;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
   
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        usuarioService.register(usuario);
        return ResponseEntity.ok("Usuario registrado");
    }
    
    
    @GetMapping("/me")
    public ResponseEntity<?> getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String email = authentication.getName(); // El email del usuario autenticado
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        return ResponseEntity.ok(usuario);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = usuarioService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: No se pudo generar el token");
        }

        System.out.println("🟢 Token generado: " + token); // Log para verificar el token

        return ResponseEntity.ok(new LoginResponse(token));
    }
    
 // UsuarioController.java - Agregando endpoint para cambiar contraseña
    @PutMapping("/cambiar-password")
    public ResponseEntity<String> cambiarPassword(@RequestBody CambiarPasswordRequest request, Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        usuarioService.cambiarPassword(usuario, request.getNuevaPassword());
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
    
    @PutMapping("/{id}/logo")
    public ResponseEntity<?> subirLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Usuario usuario = usuarioService.findById(id);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Guardar archivo en la carpeta uploads
            String nombreArchivo = "uploads/user_" + id + "_" + file.getOriginalFilename();
            Path path = Paths.get(nombreArchivo);
            Files.write(path, file.getBytes());

            // Actualizar la URL en el usuario
            usuario.setLogoUrl("/" + nombreArchivo);
            usuarioService.save(usuario); // ⬅️ Guardar el usuario con la URL actualizada

            return ResponseEntity.ok(usuario.getLogoUrl());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen");
        }
    
    }
    
}



