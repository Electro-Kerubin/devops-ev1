package org.sanosysalvos.controller;

import org.junit.jupiter.api.Test;
import org.sanosysalvos.entity.Comuna;
import org.sanosysalvos.entity.Coordenada;
import org.sanosysalvos.entity.MapaDeCalor;
import org.sanosysalvos.repository.CoordenadaRepository;
import org.sanosysalvos.repository.MapaDeCalorRepository;
import org.sanosysalvos.security.JwtFilter;
import org.sanosysalvos.security.JwtTestSupport;
import org.sanosysalvos.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GeoController.class, properties = "jwt.secret=" + JwtTestSupport.SECRET)
@Import({SecurityConfig.class, JwtFilter.class})
class GeoControllerTest {

    private static final String BEARER = "Bearer " + JwtTestSupport.validToken();

    @Autowired private MockMvc mockMvc;
    @MockitoBean private CoordenadaRepository coordenadaRepository;
    @MockitoBean private MapaDeCalorRepository mapaDeCalorRepository;

    private Coordenada coordenada() {
        Comuna comuna = Comuna.builder().idComuna(2L).nombreComuna("Valparaíso").build();
        return Coordenada.builder()
                .idUbicacionCoordenadas(1L)
                .ubicacionLat(-33.045)
                .ubicacionLon(-71.610)
                .idReporte(10L)
                .comuna(comuna)
                .direccion("Av. Principal 123")
                .build();
    }

    @Test
    void sinToken_debeResponder403() throws Exception {
        mockMvc.perform(get("/api/geo/heatmap")).andExpect(status().isForbidden());
    }

    @Test
    void coordenadas_debeEntregarFeatureCollectionConLonLatEnEseOrden() throws Exception {
        when(coordenadaRepository.findAll()).thenReturn(List.of(coordenada()));

        mockMvc.perform(get("/api/geo/coordenadas").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features[0].type").value("Feature"))
                .andExpect(jsonPath("$.features[0].geometry.type").value("Point"))
                // GeoJSON exige [longitud, latitud]
                .andExpect(jsonPath("$.features[0].geometry.coordinates[0]").value(-71.610))
                .andExpect(jsonPath("$.features[0].geometry.coordinates[1]").value(-33.045))
                .andExpect(jsonPath("$.features[0].properties.nombreComuna").value("Valparaíso"));
    }

    @Test
    void coordenadasPorComuna_debeFiltrarYEntregarGeoJson() throws Exception {
        when(coordenadaRepository.findByComuna_IdComuna(2L)).thenReturn(List.of(coordenada()));

        mockMvc.perform(get("/api/geo/coordenadas/comuna/2").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.features.length()").value(1))
                .andExpect(jsonPath("$.features[0].properties.idReporte").value(10));
    }

    @Test
    void heatmap_debeDecodificarGeohashAValoresLatLon() throws Exception {
        MapaDeCalor mapa = MapaDeCalor.builder().geohash("66jc").cantidadReportes(7).build();
        when(mapaDeCalorRepository.findAllByOrderByCantidadReportesDesc()).thenReturn(List.of(mapa));

        mockMvc.perform(get("/api/geo/heatmap").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].geohash").value("66jc"))
                .andExpect(jsonPath("$[0].cantidadReportes").value(7))
                .andExpect(jsonPath("$[0].lat").isNumber())
                .andExpect(jsonPath("$[0].lon").isNumber());
    }

    @Test
    void heatmap_conGeohashNulo_debeDevolverCeroCeroSinFallar() throws Exception {
        MapaDeCalor invalido = MapaDeCalor.builder().geohash(null).cantidadReportes(1).build();
        when(mapaDeCalorRepository.findAllByOrderByCantidadReportesDesc()).thenReturn(List.of(invalido));

        mockMvc.perform(get("/api/geo/heatmap").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lat").value(0.0))
                .andExpect(jsonPath("$[0].lon").value(0.0));
    }
}
