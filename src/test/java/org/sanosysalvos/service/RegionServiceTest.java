package org.sanosysalvos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanosysalvos.dto.RegionDTO;
import org.sanosysalvos.entity.Region;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock private RegionRepository regionRepository;

    @InjectMocks private RegionService regionService;

    private Region region;

    @BeforeEach
    void setUp() {
        region = Region.builder().idRegion(5L).nombreRegion("Valparaíso").build();
    }

    @Test
    void findAll_debeRetornarTodasLasRegiones() {
        when(regionRepository.findAll()).thenReturn(List.of(region));

        List<RegionDTO> result = regionService.findAll();

        assertEquals(1, result.size());
        assertEquals("Valparaíso", result.get(0).getNombreRegion());
    }

    @Test
    void findById_debeRetornarRegion_cuandoExiste() {
        when(regionRepository.findById(5L)).thenReturn(Optional.of(region));

        assertEquals(5L, regionService.findById(5L).getIdRegion());
    }

    @Test
    void findById_debeLanzarException_cuandoNoExiste() {
        when(regionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> regionService.findById(99L));
    }

    @Test
    void save_debePersistirRegion() {
        when(regionRepository.save(any(Region.class))).thenReturn(region);

        RegionDTO result = regionService.save(RegionDTO.builder().nombreRegion("Valparaíso").build());

        assertEquals(5L, result.getIdRegion());
        verify(regionRepository).save(any(Region.class));
    }

    @Test
    void update_debeCambiarElNombre() {
        when(regionRepository.findById(5L)).thenReturn(Optional.of(region));
        when(regionRepository.save(region)).thenReturn(region);

        RegionDTO result = regionService.update(5L, RegionDTO.builder().nombreRegion("V Región").build());

        assertEquals("V Región", result.getNombreRegion());
    }

    @Test
    void update_debeLanzarException_cuandoNoExiste() {
        when(regionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> regionService.update(99L, RegionDTO.builder().nombreRegion("X").build()));
        verify(regionRepository, never()).save(any());
    }

    @Test
    void delete_debeEliminar_cuandoExiste() {
        when(regionRepository.findById(5L)).thenReturn(Optional.of(region));

        regionService.delete(5L);

        verify(regionRepository).deleteById(5L);
    }

    @Test
    void delete_debeLanzarException_cuandoNoExiste() {
        when(regionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> regionService.delete(99L));
        verify(regionRepository, never()).deleteById(any());
    }
}
