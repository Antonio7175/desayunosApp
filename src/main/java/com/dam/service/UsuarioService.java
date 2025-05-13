package com.dam.service;

import com.dam.entity.Rol;
import com.dam.entity.Usuario;
import com.dam.repository.UsuarioRepository;
import com.dam.security.JwtTokenUtil;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UsuarioService(UsuarioRepository usuarioRepository, JwtTokenUtil jwtTokenUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Devuelve un UserDetails con las credenciales y roles del usuario
        return new User(usuario.getEmail(), usuario.getPassword(),
                usuario.getRol() == Rol.ADMIN ?
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")) :
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
    
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }
    
 // UsuarioService.java - Método para cambiar la contraseña
    public void cambiarPassword(Usuario usuario, String nuevaPassword) {
        usuario.setPassword(nuevaPassword); // Asegúrate de codificarla en el futuro
        usuarioRepository.save(usuario);
    }


    public void register(Usuario usuario) {
        // Verificar si el email ya existe
        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        // Codificar la contraseña antes de guardar (recomendado)
        // Por simplicidad aquí se guarda tal cual, pero es preferible usar:
        // String encodedPassword = passwordEncoder.encode(usuario.getPassword());
        // usuario.setPassword(encodedPassword);

        usuarioRepository.save(usuario);
    }

    public String authenticate(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            System.out.println("⚠️ Usuario con email '" + email + "' no encontrado en la base de datos.");
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        
        System.out.println("✅ Usuario encontrado: " + usuario.getEmail());
        System.out.println("🔑 Contraseña en DB: " + usuario.getPassword());
        System.out.println("🔑 Contraseña ingresada: " + password);

        if (!usuario.getPassword().equals(password)) {
            System.out.println("❌ Las contraseñas NO coinciden.");
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        System.out.println("🎉 Autenticación exitosa para: " + usuario.getEmail());
        return jwtTokenUtil.generateToken(email);
    }
    
    public Usuario findById(Long id) { // ⬅️ Nuevo método para buscar por ID
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario save(Usuario usuario) { // ⬅️ Nuevo método para guardar cambios
        return usuarioRepository.save(usuario);
    }

}
