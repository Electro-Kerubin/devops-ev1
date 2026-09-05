package org.sanosysalvos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.ComunaDTO;
import org.sanosysalvos.service.ComunaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunas")
@RequiredArgsConstructor
public class ComunaController {

    private final ComunaService comunaService;

    @GetMapping
    public ResponseEntity<List<ComunaDTO>> findAll() {
        return ResponseEntity.ok(comunaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComunaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(comunaService.findById(id));
    }

    @GetMapping("/region/{idRegion}")
    public ResponseEntity<List<ComunaDTO>> findByRegion(@PathVariable Long idRegion) {
        return ResponseEntity.ok(comunaService.findByRegion(idRegion));
    }

    @PostMapping
    public ResponseEntity<ComunaDTO> save(@Valid @RequestBody ComunaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comunaService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComunaDTO> update(@PathVariable Long id, @Valid @RequestBody ComunaDTO dto) {
        return ResponseEntity.ok(comunaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        comunaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

