package org.sanosysalvos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.MapaDeCalorDTO;
import org.sanosysalvos.service.MapaDeCalorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mapa-calor")
@RequiredArgsConstructor
public class MapaDeCalorController {

    private final MapaDeCalorService mapaDeCalorService;

    @GetMapping
    public ResponseEntity<List<MapaDeCalorDTO>> findAll() {
        return ResponseEntity.ok(mapaDeCalorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MapaDeCalorDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mapaDeCalorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MapaDeCalorDTO> save(@Valid @RequestBody MapaDeCalorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapaDeCalorService.save(dto));
    }

    @PostMapping("/incrementar/{geohash}")
    public ResponseEntity<MapaDeCalorDTO> incrementar(@PathVariable String geohash) {
        return ResponseEntity.ok(mapaDeCalorService.incrementarReporte(geohash));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mapaDeCalorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

