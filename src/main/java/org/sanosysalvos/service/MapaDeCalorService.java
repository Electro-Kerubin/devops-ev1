package org.sanosysalvos.service;

import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.MapaDeCalorDTO;
import org.sanosysalvos.entity.MapaDeCalor;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.repository.MapaDeCalorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class MapaDeCalorService {

    private final MapaDeCalorRepository mapaDeCalorRepository;

    public List<MapaDeCalorDTO> findAll() {
        return mapaDeCalorRepository.findAllByOrderByCantidadReportesDesc().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public MapaDeCalorDTO findById(Long id) {
        return toDTO(mapaDeCalorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MapaDeCalor no encontrado con id: " + id)));
    }

    @Transactional
    public MapaDeCalorDTO save(MapaDeCalorDTO dto) {
        MapaDeCalor mapa = MapaDeCalor.builder()
                .geohash(dto.getGeohash())
                .cantidadReportes(dto.getCantidadReportes())
                .lastCalculatedAt(LocalDateTime.now())
                .build();
        return toDTO(mapaDeCalorRepository.save(mapa));
    }

    @Transactional
    public MapaDeCalorDTO incrementarReporte(String geohash) {
        MapaDeCalor mapa = mapaDeCalorRepository.findByGeohash(geohash)
                .orElse(MapaDeCalor.builder().geohash(geohash).cantidadReportes(0).build());
        mapa.setCantidadReportes(mapa.getCantidadReportes() + 1);
        mapa.setLastCalculatedAt(LocalDateTime.now());
        return toDTO(mapaDeCalorRepository.save(mapa));
    }

    @Transactional
    public void delete(Long id) {
        mapaDeCalorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MapaDeCalor no encontrado con id: " + id));
        mapaDeCalorRepository.deleteById(id);
    }

    private MapaDeCalorDTO toDTO(MapaDeCalor m) {
        return MapaDeCalorDTO.builder()
                .idMapaDeCalor(m.getIdMapaDeCalor())
                .geohash(m.getGeohash())
                .cantidadReportes(m.getCantidadReportes())
                .lastCalculatedAt(m.getLastCalculatedAt())
                .build();
    }
}
