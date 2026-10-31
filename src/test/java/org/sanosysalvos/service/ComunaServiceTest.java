package org.sanosysalvos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanosysalvos.dto.ComunaDTO;
import org.sanosysalvos.entity.Comuna;
import org.sanosysalvos.entity.Region;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.repository.ComunaRepository;
import org.sanosysalvos.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunaServiceTest {

    @Mock private ComunaRepository comunaRepository;
    @Mock private RegionRepository regionRepository;

    @InjectMocks private ComunaService comunaService;

    private Region region;
    private Comuna comuna;

    @BeforeEach
    void setUp() {
        region = Region.builder().idRegion(5L).nombreRegion("Valparaíso").build();
        comuna = Comuna.builder().idComuna(1L).nombreComuna("Viña del Mar").region(region).build();
    }

    @Test
    void findAll_debeMapearTodasLasComunas() {
        when(comunaRepository.findAll()).thenReturn(List.of(comuna));

        List<ComunaDTO> result = comunaService.findAll();

        assertEquals(1, result.size());
        assertEquals("Viña del Mar", result.get(0).getNombreComuna());
        assertEquals(5L, result.get(0).getIdRegion());
    }

    @Test
    void findByRegion_debeFiltrarPorRegion() {
        when(comunaRepository.findByRegion_IdRegion(5L)).thenReturn(List.of(comuna));

        List<ComunaDTO> result = comunaService.findByRegion(5L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdComuna());
    }

    @Test
    void findById_debeRetornarComuna_cuandoExiste() {
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(comuna));

        assertEquals("Viña del Mar", comunaService.findById(1L).getNombreComuna());
    }

    @Test
    void findById_debeLanzarException_cuandoNoExiste() {
        when(comunaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> comunaService.findById(99L));
    }

    @Test
    void save_debeGuardarComunaConSuRegion() {
        ComunaDTO dto = ComunaDTO.builder().nombreComuna("Viña del Mar").idRegion(5L).build();
        when(regionRepository.findById(5L)).thenReturn(Optional.of(region));
        when(comunaRepository.save(any(Comuna.class))).thenReturn(comuna);

        ComunaDTO result = comunaService.save(dto);

        assertEquals(1L, result.getIdComuna());
        verify(comunaRepository).save(any(Comuna.class));
    }

    @Test
    void save_debeLanzarException_cuandoRegionNoExiste() {
        ComunaDTO dto = ComunaDTO.builder().nombreComuna("X").idRegion(99L).build();
        when(regionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> comunaService.save(dto));
        verify(comunaRepository, never()).save(any());
    }

    @Test
    void update_debeActualizarNombreYRegion() {
        ComunaDTO dto = ComunaDTO.builder().nombreComuna("Quilpué").idRegion(5L).build();
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(comuna));
        when(regionRepository.findById(5L)).thenReturn(Optional.of(region));
        when(comunaRepository.save(comuna)).thenReturn(comuna);

        ComunaDTO result = comunaService.update(1L, dto);

        assertEquals("Quilpué", result.getNombreComuna());
    }

    @Test
    void update_debeLanzarException_cuandoComunaNoExiste() {
        when(comunaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> comunaService.update(99L, ComunaDTO.builder().idRegion(5L).build()));
    }

    @Test
    void update_debeLanzarException_cuandoRegionNoExiste() {
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(comuna));
        when(regionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> comunaService.update(1L, ComunaDTO.builder().idRegion(99L).build()));
    }

    @Test
    void delete_debeEliminar_cuandoExiste() {
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(comuna));

        comunaService.delete(1L);

        verify(comunaRepository).deleteById(1L);
    }

    @Test
    void delete_debeLanzarException_cuandoNoExiste() {
        when(comunaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> comunaService.delete(99L));
        verify(comunaRepository, never()).deleteById(any());
    }
}
