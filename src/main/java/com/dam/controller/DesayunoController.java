package com.dam.controller;

import com.dam.entity.Desayuno;
import com.dam.service.DesayunoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/desayunos")
public class DesayunoController {

    private final DesayunoService desayunoService;

    public DesayunoController(DesayunoService desayunoService) {
        this.desayunoService = desayunoService;
    }

    @GetMapping
    public ResponseEntity<List<Desayuno>> getAllDesayunos() {
        return ResponseEntity.ok(desayunoService.getAllDesayunos());
    }

    @GetMapping("/{desayunoId}")
    public ResponseEntity<Desayuno> getDesayunoById(@PathVariable Long desayunoId) {
        return desayunoService.getDesayunoById(desayunoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/{barId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Desayuno> createDesayuno(@PathVariable Long barId, @RequestBody Desayuno desayuno) {
        // Extraemos la URL de la imagen desde el objeto desayuno (si está disponible)
        String imagenUrl = desayuno.getImagenUrl() != null ? desayuno.getImagenUrl() : "";
        
        Desayuno nuevoDesayuno = desayunoService.createDesayuno(barId, desayuno, imagenUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDesayuno);
    }

    
    
    @PutMapping("/{desayunoId}/imagen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateDesayunoImage(@PathVariable Long desayunoId, @RequestParam("file") MultipartFile file) {
        String imageUrl = desayunoService.saveImage(desayunoId, file);
        return ResponseEntity.ok(imageUrl);
    }


    @PutMapping("/{desayunoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Desayuno> updateDesayuno(@PathVariable Long desayunoId, @RequestBody Desayuno desayuno) {
        return ResponseEntity.ok(desayunoService.updateDesayuno(desayunoId, desayuno));
    }

    @DeleteMapping("/{desayunoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDesayuno(@PathVariable Long desayunoId) {
        desayunoService.deleteDesayuno(desayunoId);
        return ResponseEntity.ok("Desayuno eliminado");
    }
}
