package org.sanosysalvos.controller;

import org.junit.jupiter.api.Test;
import org.sanosysalvos.dto.ComunaDTO;
import org.sanosysalvos.dto.MapaDeCalorDTO;
import org.sanosysalvos.dto.RegionDTO;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.security.JwtFilter;
import org.sanosysalvos.security.JwtTestSupport;
import org.sanosysalvos.security.SecurityConfig;
import org.sanosysalvos.service.ComunaService;
import org.sanosysalvos.service.MapaDeCalorService;
import org.sanosysalvos.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {ComunaController.class, RegionController.class, MapaDeCalorController.class},
        properties = "jwt.secret=" + JwtTestSupport.SECRET)
@Import({SecurityConfig.class, JwtFilter.class})
class ComunaRegionMapaControllerTest {

    private static final String BEARER = "Bearer " + JwtTestSupport.validToken();
    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ComunaService comunaService;
    @MockitoBean private RegionService regionService;
    @MockitoBean private MapaDeCalorService mapaDeCalorService;

    // ---------- Comuna ----------

    @Test
    void comunas_sinToken_debeResponder403() throws Exception {
        mockMvc.perform(get("/api/comunas")).andExpect(status().isForbidden());
    }

    @Test
    void comunas_findAll_debeResponder200() throws Exception {
        when(comunaService.findAll()).thenReturn(List.of(
                ComunaDTO.builder().idComuna(1L).nombreComuna("Viña del Mar").idRegion(5L).build()));

        mockMvc.perform(get("/api/comunas").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreComuna").value("Viña del Mar"));
    }

    @Test
    void comunas_findById_inexistente_debeResponder404() throws Exception {
        when(comunaService.findById(99L)).thenThrow(new ResourceNotFoundException("Comuna no encontrada con id: 99"));

        mockMvc.perform(get("/api/comunas/99").header("Authorization", BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Comuna no encontrada con id: 99"));
    }

    @Test
    void comunas_findByRegion_debeResponder200() throws Exception {
        when(comunaService.findByRegion(5L)).thenReturn(List.of(ComunaDTO.builder().idComuna(1L).build()));

        mockMvc.perform(get("/api/comunas/region/5").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void comunas_save_valido_debeResponder201() throws Exception {
        when(comunaService.save(any())).thenReturn(ComunaDTO.builder().idComuna(1L).nombreComuna("Quilpué").idRegion(5L).build());

        mockMvc.perform(post("/api/comunas").header("Authorization", BEARER).contentType(JSON)
                        .content("{\"nombreComuna\":\"Quilpué\",\"idRegion\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idComuna").value(1));
    }

    @Test
    void comunas_save_invalido_debeResponder400() throws Exception {
        mockMvc.perform(post("/api/comunas").header("Authorization", BEARER).contentType(JSON)
                        .content("{\"nombreComuna\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombreComuna").value("El nombre de la comuna es obligatorio"))
                .andExpect(jsonPath("$.idRegion").value("El id de la región es obligatorio"));
    }

    @Test
    void comunas_update_y_delete() throws Exception {
        when(comunaService.update(eq(1L), any())).thenReturn(ComunaDTO.builder().idComuna(1L).build());

        mockMvc.perform(put("/api/comunas/1").header("Authorization", BEARER).contentType(JSON)
                        .content("{\"nombreComuna\":\"Quilpué\",\"idRegion\":5}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/comunas/1").header("Authorization", BEARER))
                .andExpect(status().isNoContent());
        verify(comunaService).delete(1L);
    }

    // ---------- Región ----------

    @Test
    void regiones_findAll_y_findById() throws Exception {
        RegionDTO region = RegionDTO.builder().idRegion(5L).nombreRegion("Valparaíso").build();
        when(regionService.findAll()).thenReturn(List.of(region));
        when(regionService.findById(5L)).thenReturn(region);

        mockMvc.perform(get("/api/regiones").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreRegion").value("Valparaíso"));
        mockMvc.perform(get("/api/regiones/5").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRegion").value(5));
    }

    @Test
    void regiones_findById_inexistente_debeResponder404() throws Exception {
        when(regionService.findById(99L)).thenThrow(new ResourceNotFoundException("Región no encontrada con id: 99"));

        mockMvc.perform(get("/api/regiones/99").header("Authorization", BEARER))
                .andExpect(status().isNotFound());
    }

    @Test
    void regiones_save_valido_debeResponder201() throws Exception {
        when(regionService.save(any())).thenReturn(RegionDTO.builder().idRegion(5L).nombreRegion("Valparaíso").build());

        mockMvc.perform(post("/api/regiones").header("Authorization", BEARER).contentType(JSON)
                        .content("{\"nombreRegion\":\"Valparaíso\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void regiones_save_invalido_debeResponder400() throws Exception {
        mockMvc.perform(post("/api/regiones").header("Authorization", BEARER).contentType(JSON)
                        .content("{\"nombreRegion\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombreRegion").value("El nombre de la región es obligatorio"));
    }

    @Test
    void regiones_update_y_delete() throws Exception {
        when(regionService.update(eq(5L), any())).thenReturn(RegionDTO.builder().idRegion(5L).build());

        mockMvc.perform(put("/api/regiones/5").header("Authorization", BEARER).contentType(JSON)
                        .content("{\"nombreRegion\":\"V Región\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/regiones/5").header("Authorization", BEARER))
                .andExpect(status().isNoContent());
        verify(regionService).delete(5L);
    }

    // ---------- Mapa de calor ----------

    @Test
    void mapaCalor_findAll_y_findById() throws Exception {
        MapaDeCalorDTO mapa = MapaDeCalorDTO.builder().idMapaDeCalor(1L).geohash("66jc").cantidadReportes(3).build();
        when(mapaDeCalorService.findAll()).thenReturn(List.of(mapa));
        when(mapaDeCalorService.findById(1L)).thenReturn(mapa);

        mockMvc.perform(get("/api/mapa-calor").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].geohash").value("66jc"));
        mockMvc.perform(get("/api/mapa-calor/1").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadReportes").value(3));
    }

    @Test
    void mapaCalor_save_valido_debeResponder201() throws Exception {
        when(mapaDeCalorService.save(any())).thenReturn(MapaDeCalorDTO.builder().idMapaDeCalor(1L).build());

        mockMvc.perform(post("/api/mapa-calor").header("Authorization", BEARER).contentType(JSON)
                        .content("{\"geohash\":\"66jc\",\"cantidadReportes\":3}"))
                .andExpect(status().isCreated());
    }

    @Test
    void mapaCalor_save_invalido_debeResponder400() throws Exception {
        mockMvc.perform(post("/api/mapa-calor").header("Authorization", BEARER).contentType(JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.geohash").value("El geohash es obligatorio"))
                .andExpect(jsonPath("$.cantidadReportes").value("La cantidad de reportes es obligatoria"));
    }

    @Test
    void mapaCalor_incrementar_debeResponder200() throws Exception {
        when(mapaDeCalorService.incrementarReporte("66jc"))
                .thenReturn(MapaDeCalorDTO.builder().geohash("66jc").cantidadReportes(4).build());

        mockMvc.perform(post("/api/mapa-calor/incrementar/66jc").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadReportes").value(4));
    }

    @Test
    void mapaCalor_delete_debeResponder204() throws Exception {
        mockMvc.perform(delete("/api/mapa-calor/1").header("Authorization", BEARER))
                .andExpect(status().isNoContent());
        verify(mapaDeCalorService).delete(1L);
    }
}
