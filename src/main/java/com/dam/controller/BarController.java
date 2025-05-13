package com.dam.controller;

import com.dam.dto.BarDTO;
import com.dam.entity.Bar;
import com.dam.entity.Desayuno;
import com.dam.service.BarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/bares")
public class BarController {

    private final BarService barService;

    public BarController(BarService barService) {
        this.barService = barService;
    }

    @GetMapping
    public ResponseEntity<List<Bar>> getAllBars() {
        return ResponseEntity.ok(barService.getAllBars());
    }

    @GetMapping("/{barId}/menu")
    public ResponseEntity<List<Desayuno>> getDesayunosByBar(@PathVariable Long barId) {
        List<Desayuno> desayunos = barService.getDesayunosByBar(barId);
        return ResponseEntity.ok(desayunos);
    }

    @GetMapping("/{barId}")
    public ResponseEntity<BarDTO> getBarById(@PathVariable Long barId) {
        return barService.getBarById(barId)
                .map(bar -> ResponseEntity.ok(new BarDTO(bar)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bar> createBar(@RequestBody Bar bar, @AuthenticationPrincipal UserDetails userDetails) {
        Bar nuevoBar = barService.createBar(userDetails.getUsername(), bar);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBar);
    }

    @PutMapping("/{barId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bar> updateBar(@PathVariable Long barId, @RequestBody Bar barDetails) {
        return ResponseEntity.ok(barService.updateBar(barId, barDetails));
    }


    @DeleteMapping("/{barId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBar(@PathVariable Long barId) {
        barService.deleteBar(barId);
        return ResponseEntity.ok("Bar eliminado");
    }

    @PostMapping("/{barId}/desayunos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bar> addDesayuno(@PathVariable Long barId, @RequestBody Desayuno desayuno) {
        return ResponseEntity.ok(barService.addDesayuno(barId, desayuno));
    }

    @PutMapping("/{barId}/imagen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadOrUpdateImage(@PathVariable Long barId, @RequestParam("file") MultipartFile file) {
        String imageUrl = barService.saveImage(barId, file);
        return ResponseEntity.ok(imageUrl);
    }
}
