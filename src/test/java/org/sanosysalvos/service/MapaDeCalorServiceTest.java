package org.sanosysalvos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanosysalvos.dto.MapaDeCalorDTO;
import org.sanosysalvos.entity.MapaDeCalor;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.repository.MapaDeCalorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapaDeCalorServiceTest {

    @Mock private MapaDeCalorRepository mapaDeCalorRepository;

    @InjectMocks private MapaDeCalorService mapaDeCalorService;

    private MapaDeCalor mapa;

    @BeforeEach
    void setUp() {
        mapa = MapaDeCalor.builder()
                .idMapaDeCalor(1L)
                .geohash("66jc")
                .cantidadReportes(3)
                .lastCalculatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_debeRetornarOrdenadoPorCantidad() {
        when(mapaDeCalorRepository.findAllByOrderByCantidadReportesDesc()).thenReturn(List.of(mapa));

        List<MapaDeCalorDTO> result = mapaDeCalorService.findAll();

        assertEquals(1, result.size());
        assertEquals("66jc", result.get(0).getGeohash());
    }

    @Test
    void findById_debeRetornarMapa_cuandoExiste() {
        when(mapaDeCalorRepository.findById(1L)).thenReturn(Optional.of(mapa));

        assertEquals(3, mapaDeCalorService.findById(1L).getCantidadReportes());
    }

    @Test
    void findById_debeLanzarException_cuandoNoExiste() {
        when(mapaDeCalorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mapaDeCalorService.findById(99L));
    }

    @Test
    void save_debeGuardarConFechaDeCalculo() {
        MapaDeCalorDTO dto = MapaDeCalorDTO.builder().geohash("66jc").cantidadReportes(3).build();
        when(mapaDeCalorRepository.save(any(MapaDeCalor.class))).thenReturn(mapa);

        mapaDeCalorService.save(dto);

        ArgumentCaptor<MapaDeCalor> captor = ArgumentCaptor.forClass(MapaDeCalor.class);
        verify(mapaDeCalorRepository).save(captor.capture());
        assertEquals("66jc", captor.getValue().getGeohash());
        assertNotNull(captor.getValue().getLastCalculatedAt());
    }

    @Test
    void incrementarReporte_debeSumarUno_cuandoElGeohashExiste() {
        when(mapaDeCalorRepository.findByGeohash("66jc")).thenReturn(Optional.of(mapa));
        when(mapaDeCalorRepository.save(mapa)).thenReturn(mapa);

        MapaDeCalorDTO result = mapaDeCalorService.incrementarReporte("66jc");

        assertEquals(4, result.getCantidadReportes());
    }

    @Test
    void incrementarReporte_debeCrearConUno_cuandoElGeohashNoExiste() {
        when(mapaDeCalorRepository.findByGeohash("nuevo")).thenReturn(Optional.empty());
        when(mapaDeCalorRepository.save(any(MapaDeCalor.class))).thenAnswer(inv -> inv.getArgument(0));

        MapaDeCalorDTO result = mapaDeCalorService.incrementarReporte("nuevo");

        assertEquals("nuevo", result.getGeohash());
        assertEquals(1, result.getCantidadReportes());
    }

    @Test
    void delete_debeEliminar_cuandoExiste() {
        when(mapaDeCalorRepository.findById(1L)).thenReturn(Optional.of(mapa));

        mapaDeCalorService.delete(1L);

        verify(mapaDeCalorRepository).deleteById(1L);
    }

    @Test
    void delete_debeLanzarException_cuandoNoExiste() {
        when(mapaDeCalorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mapaDeCalorService.delete(99L));
        verify(mapaDeCalorRepository, never()).deleteById(any());
    }
}
