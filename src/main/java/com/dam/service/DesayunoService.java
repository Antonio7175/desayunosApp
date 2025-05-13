package com.dam.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dam.entity.Desayuno;
import com.dam.repository.BarRepository;
import com.dam.repository.DesayunoRepository;

@Service
public class DesayunoService {
    private final DesayunoRepository desayunoRepository;
    private final BarRepository barRepository;

    public DesayunoService(DesayunoRepository desayunoRepository, BarRepository barRepository) {
        this.desayunoRepository = desayunoRepository;
        this.barRepository = barRepository;
    }

    public List<Desayuno> getAllDesayunos() {
        return desayunoRepository.findAll();
    }

    public Optional<Desayuno> getDesayunoById(Long id) {
        return desayunoRepository.findById(id);
    }

    
 
    public Desayuno createDesayuno(Long barId, Desayuno desayuno, String imagenUrl) {
        return barRepository.findById(barId).map(bar -> {
            desayuno.setBar(bar);
            desayuno.setImagenUrl(imagenUrl);
            return desayunoRepository.save(desayuno);
        }).orElseThrow(() -> new RuntimeException("Bar no encontrado con ID: " + barId));
    }




    public Desayuno saveDesayuno(Desayuno desayuno) {
        return desayunoRepository.save(desayuno);
    }

    public Desayuno updateDesayuno(Long id, Desayuno desayunoDetails) {
        return desayunoRepository.findById(id)
            .map(desayuno -> {
                desayuno.setNombre(desayunoDetails.getNombre());
                desayuno.setDescripcion(desayunoDetails.getDescripcion());
                desayuno.setPrecio(desayunoDetails.getPrecio());
                return desayunoRepository.save(desayuno);
            }).orElseThrow(() -> new RuntimeException("Desayuno no encontrado"));
    }

    public void deleteDesayuno(Long id) {
        desayunoRepository.deleteById(id);
    }
    
    public String saveImage(Long desayunoId, MultipartFile file) {
        return desayunoRepository.findById(desayunoId).map(desayuno -> {
            try {
                String fileName = "uploads/desayunos/" + desayunoId + "_" + file.getOriginalFilename();
                Path path = Paths.get(fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                String imageUrl = "/uploads/desayunos/" + desayunoId + "_" + file.getOriginalFilename();
                desayuno.setImagenUrl(imageUrl);
                desayunoRepository.save(desayuno);
                return imageUrl;
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar la imagen", e);
            }
        }).orElseThrow(() -> new RuntimeException("Desayuno no encontrado"));
    }

}
