package com.dam.service;

import com.dam.entity.Bar;
import com.dam.entity.Desayuno;
import com.dam.repository.BarRepository;
import com.dam.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class BarService {

    private final BarRepository barRepository;
    private final UsuarioRepository usuarioRepository;

    public BarService(BarRepository barRepository, UsuarioRepository usuarioRepository) {
        this.barRepository = barRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Bar> getAllBars() {
        return barRepository.findAll();
    }

    public Optional<Bar> getBarById(Long id) {
        return barRepository.findById(id);
    }

    public Bar createBar(String emailPropietario, Bar bar) {
        return usuarioRepository.findByEmail(emailPropietario).map(usuario -> {
            bar.setPropietario(usuario);
            return barRepository.save(bar);
        }).orElseThrow(() -> new RuntimeException("Usuario propietario no encontrado"));
    }

    public List<Desayuno> getDesayunosByBar(Long barId) {
        return barRepository.findById(barId)
                .map(Bar::getDesayunos)
                .orElse(List.of()); // Devuelve lista vacía si no hay desayunos
    }

    public Bar updateBar(Long id, Bar barDetails) {
        return barRepository.findById(id).map(bar -> {
            bar.setNombre(barDetails.getNombre());
            bar.setDireccion(barDetails.getDireccion());
            return barRepository.save(bar);
        }).orElseThrow(() -> new RuntimeException("Bar no encontrado"));
    }

    public void deleteBar(Long id) {
        barRepository.deleteById(id);
    }

    public Bar addDesayuno(Long barId, Desayuno desayuno) {
        return barRepository.findById(barId).map(bar -> {
            desayuno.setBar(bar);
            bar.getDesayunos().add(desayuno);
            return barRepository.save(bar);
        }).orElseThrow(() -> new RuntimeException("Bar no encontrado"));
    }

    public String saveImage(Long barId, MultipartFile file) {
        return barRepository.findById(barId).map(bar -> {
            try {
                String fileName = "uploads/bars/" + barId + "_" + file.getOriginalFilename();
                Path path = Paths.get(fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                String imageUrl = "/uploads/bars/" + barId + "_" + file.getOriginalFilename();
                bar.setImagenUrl(imageUrl);
                barRepository.save(bar);
                return imageUrl;
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar la imagen", e);
            }
        }).orElseThrow(() -> new RuntimeException("Bar no encontrado"));
    }
}
