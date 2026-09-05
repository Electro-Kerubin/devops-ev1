package org.sanosysalvos.service;

import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.RegionDTO;
import org.sanosysalvos.entity.Region;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class RegionService {

    private final RegionRepository regionRepository;

    public List<RegionDTO> findAll() {
        return regionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RegionDTO findById(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada con id: " + id));
        return toDTO(region);
    }

    @Transactional
    public RegionDTO save(RegionDTO dto) {
        Region region = Region.builder()
                .nombreRegion(dto.getNombreRegion())
                .build();
        return toDTO(regionRepository.save(region));
    }

    @Transactional
    public RegionDTO update(Long id, RegionDTO dto) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada con id: " + id));
        region.setNombreRegion(dto.getNombreRegion());
        return toDTO(regionRepository.save(region));
    }

    @Transactional
    public void delete(Long id) {
        regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada con id: " + id));
        regionRepository.deleteById(id);
    }

    private RegionDTO toDTO(Region r) {
        return RegionDTO.builder()
                .idRegion(r.getIdRegion())
                .nombreRegion(r.getNombreRegion())
                .build();
    }
}
