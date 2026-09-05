package org.sanosysalvos.service;

import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.ComunaDTO;
import org.sanosysalvos.entity.Comuna;
import org.sanosysalvos.entity.Region;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.repository.ComunaRepository;
import org.sanosysalvos.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ComunaService {

    private final ComunaRepository comunaRepository;
    private final RegionRepository regionRepository;

    public List<ComunaDTO> findAll() {
        return comunaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ComunaDTO> findByRegion(Long idRegion) {
        return comunaRepository.findByRegion_IdRegion(idRegion).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ComunaDTO findById(Long id) {
        return toDTO(comunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada con id: " + id)));
    }

    @Transactional
    public ComunaDTO save(ComunaDTO dto) {
        Region region = regionRepository.findById(dto.getIdRegion())
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada con id: " + dto.getIdRegion()));
        Comuna comuna = Comuna.builder()
                .nombreComuna(dto.getNombreComuna())
                .region(region)
                .build();
        return toDTO(comunaRepository.save(comuna));
    }

    @Transactional
    public ComunaDTO update(Long id, ComunaDTO dto) {
        Comuna comuna = comunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada con id: " + id));
        Region region = regionRepository.findById(dto.getIdRegion())
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada con id: " + dto.getIdRegion()));
        comuna.setNombreComuna(dto.getNombreComuna());
        comuna.setRegion(region);
        return toDTO(comunaRepository.save(comuna));
    }

    @Transactional
    public void delete(Long id) {
        comunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada con id: " + id));
        comunaRepository.deleteById(id);
    }

    private ComunaDTO toDTO(Comuna c) {
        return ComunaDTO.builder()
                .idComuna(c.getIdComuna())
                .nombreComuna(c.getNombreComuna())
                .idRegion(c.getRegion().getIdRegion())
                .build();
    }
}
