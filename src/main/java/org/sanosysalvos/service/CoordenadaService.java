package org.sanosysalvos.service;

import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.CoordenadaDTO;
import org.sanosysalvos.entity.Comuna;
import org.sanosysalvos.entity.Coordenada;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.repository.ComunaRepository;
import org.sanosysalvos.repository.CoordenadaRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CoordenadaService {

    private final CoordenadaRepository coordenadaRepository;
    private final ComunaRepository comunaRepository;
    private final DataSource dataSource;

    public List<CoordenadaDTO> findAll() {
        return coordenadaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CoordenadaDTO findById(Long id) {
        return toDTO(coordenadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordenada no encontrada con id: " + id)));
    }

    public CoordenadaDTO findByReporte(Long idReporte) {
        return toDTO(coordenadaRepository.findByIdReporte(idReporte)
                .orElseThrow(() -> new ResourceNotFoundException("Coordenada no encontrada para reporte: " + idReporte)));
    }

    public List<CoordenadaDTO> findByComuna(Long idComuna) {
        return coordenadaRepository.findByComuna_IdComuna(idComuna).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public CoordenadaDTO save(CoordenadaDTO dto) {
        Comuna comuna = comunaRepository.findById(dto.getIdComuna())
                .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada con id: " + dto.getIdComuna()));
        Coordenada coordenada = Coordenada.builder()
                .ubicacionLat(dto.getUbicacionLat())
                .ubicacionLon(dto.getUbicacionLon())
                .idReporte(dto.getIdReporte())
                .comuna(comuna)
                .direccion(dto.getDireccion())
                .build();
        CoordenadaDTO saved = toDTO(coordenadaRepository.save(coordenada));

        // Desnormalizar lat/lon en reporte_mascota para el motor de coincidencias
        actualizarCoordenadaEnReporte(dto.getIdReporte(), dto.getUbicacionLat(), dto.getUbicacionLon());

        return saved;
    }

    @Transactional
    public CoordenadaDTO update(Long id, CoordenadaDTO dto) {
        Coordenada coordenada = coordenadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordenada no encontrada con id: " + id));
        Comuna comuna = comunaRepository.findById(dto.getIdComuna())
                .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada con id: " + dto.getIdComuna()));
        coordenada.setUbicacionLat(dto.getUbicacionLat());
        coordenada.setUbicacionLon(dto.getUbicacionLon());
        coordenada.setIdReporte(dto.getIdReporte());
        coordenada.setComuna(comuna);
        coordenada.setDireccion(dto.getDireccion());
        CoordenadaDTO saved = toDTO(coordenadaRepository.save(coordenada));

        // Actualizar también en reporte_mascota
        actualizarCoordenadaEnReporte(dto.getIdReporte(), dto.getUbicacionLat(), dto.getUbicacionLon());

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        coordenadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordenada no encontrada con id: " + id));
        coordenadaRepository.deleteById(id);
    }

    private void actualizarCoordenadaEnReporte(Long idReporte, Double lat, Double lon) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE reporte_mascota SET latitud = ?, longitud = ? WHERE id_reporte_mascota = ?")) {
            ps.setDouble(1, lat);
            ps.setDouble(2, lon);
            ps.setLong(3, idReporte);
            ps.executeUpdate();
        } catch (SQLException e) {
            LoggerFactory.getLogger(CoordenadaService.class)
                    .warn("No se pudo actualizar lat/lon en reporte_mascota id={}: {}", idReporte, e.getMessage());
        }
    }

    private CoordenadaDTO toDTO(Coordenada c) {
        return CoordenadaDTO.builder()
                .idUbicacionCoordenadas(c.getIdUbicacionCoordenadas())
                .ubicacionLat(c.getUbicacionLat())
                .ubicacionLon(c.getUbicacionLon())
                .idReporte(c.getIdReporte())
                .idComuna(c.getComuna().getIdComuna())
                .direccion(c.getDireccion())
                .createdAt(c.getCreatedAt())
                .build();
    }
}