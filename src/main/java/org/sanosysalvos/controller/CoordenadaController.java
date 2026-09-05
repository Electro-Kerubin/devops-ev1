package org.sanosysalvos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.CoordenadaDTO;
import org.sanosysalvos.service.CoordenadaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordenadas")
@RequiredArgsConstructor
public class CoordenadaController {

    private final CoordenadaService coordenadaService;

    @GetMapping
    public ResponseEntity<List<CoordenadaDTO>> findAll() {
        return ResponseEntity.ok(coordenadaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordenadaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(coordenadaService.findById(id));
    }

    @GetMapping("/reporte/{idReporte}")
    public ResponseEntity<CoordenadaDTO> findByReporte(@PathVariable Long idReporte) {
        return ResponseEntity.ok(coordenadaService.findByReporte(idReporte));
    }

    @GetMapping("/comuna/{idComuna}")
    public ResponseEntity<List<CoordenadaDTO>> findByComuna(@PathVariable Long idComuna) {
        return ResponseEntity.ok(coordenadaService.findByComuna(idComuna));
    }

    @PostMapping
    public ResponseEntity<CoordenadaDTO> save(@Valid @RequestBody CoordenadaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coordenadaService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordenadaDTO> update(@PathVariable Long id, @Valid @RequestBody CoordenadaDTO dto) {
        return ResponseEntity.ok(coordenadaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        coordenadaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

