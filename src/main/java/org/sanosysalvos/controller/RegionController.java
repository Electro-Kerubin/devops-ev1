package org.sanosysalvos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.RegionDTO;
import org.sanosysalvos.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regiones")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public ResponseEntity<List<RegionDTO>> findAll() {
        return ResponseEntity.ok(regionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RegionDTO> save(@Valid @RequestBody RegionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegionDTO> update(@PathVariable Long id, @Valid @RequestBody RegionDTO dto) {
        return ResponseEntity.ok(regionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

