package org.sanosysalvos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanosysalvos.dto.CoordenadaDTO;
import org.sanosysalvos.entity.Comuna;
import org.sanosysalvos.entity.Coordenada;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.repository.ComunaRepository;
import org.sanosysalvos.repository.CoordenadaRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoordenadaServiceTest {

    @Mock private CoordenadaRepository coordenadaRepository;
    @Mock private ComunaRepository comunaRepository;
    @Mock private DataSource dataSource;
    @Mock private Connection connection;
    @Mock private PreparedStatement preparedStatement;

    @InjectMocks private CoordenadaService coordenadaService;

    private Comuna comuna;
    private Coordenada coordenada;

    @BeforeEach
    void setUp() {
        comuna = new Comuna();
        comuna.setIdComuna(1L);
        comuna.setNombreComuna("Valparaíso");

        coordenada = Coordenada.builder()
            .idUbicacionCoordenadas(1L)
            .ubicacionLat(-33.045)
            .ubicacionLon(-71.610)
            .idReporte(1L)
            .comuna(comuna)
            .direccion("Av. Principal 123")
            .build();
    }

    @Test
    void findAll_debeRetornarListaDeCoordenadas() {
        when(coordenadaRepository.findAll()).thenReturn(List.of(coordenada));

        List<CoordenadaDTO> result = coordenadaService.findAll();

        assertEquals(1, result.size());
        assertEquals(-33.045, result.get(0).getUbicacionLat());
    }

    @Test
    void findById_debeRetornarCoordenada_cuandoExiste() {
        when(coordenadaRepository.findById(1L)).thenReturn(Optional.of(coordenada));

        CoordenadaDTO result = coordenadaService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdUbicacionCoordenadas());
    }

    @Test
    void findById_debeLanzarException_cuandoNoExiste() {
        when(coordenadaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> coordenadaService.findById(99L));
    }

    @Test
    void save_debeGuardarCoordenadaYActualizarReporteMascota() throws Exception {
        CoordenadaDTO dto = new CoordenadaDTO();
        dto.setUbicacionLat(-33.045);
        dto.setUbicacionLon(-71.610);
        dto.setIdReporte(1L);
        dto.setIdComuna(1L);
        dto.setDireccion("Av. Principal 123");

        when(comunaRepository.findById(1L)).thenReturn(Optional.of(comuna));
        when(coordenadaRepository.save(any())).thenReturn(coordenada);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        CoordenadaDTO result = coordenadaService.save(dto);

        assertNotNull(result);
        verify(coordenadaRepository, times(1)).save(any());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void save_debeLanzarException_cuandoComunaNoExiste() {
        CoordenadaDTO dto = new CoordenadaDTO();
        dto.setIdComuna(99L);
        dto.setIdReporte(1L);

        when(comunaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> coordenadaService.save(dto));
    }
    
}