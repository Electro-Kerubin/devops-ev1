package org.sanosysalvos.controller;

import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.UploadResponseDto;
import org.sanosysalvos.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload/{idReporte}")
    public ResponseEntity<UploadResponseDto> upload(
            @PathVariable Long idReporte,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(storageService.uploadFile(file, idReporte));
    }

    @GetMapping("/fotos/{idReporte}")
    public ResponseEntity<List<String>> getFotos(@PathVariable Long idReporte) {
        return ResponseEntity.ok(storageService.getFotosByReporte(idReporte));
    }
}