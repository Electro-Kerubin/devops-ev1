package org.sanosysalvos.controller;

import org.junit.jupiter.api.Test;
import org.sanosysalvos.dto.CoordenadaDTO;
import org.sanosysalvos.exception.ResourceNotFoundException;
import org.sanosysalvos.security.JwtFilter;
import org.sanosysalvos.security.JwtTestSupport;
import org.sanosysalvos.security.SecurityConfig;
import org.sanosysalvos.service.CoordenadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CoordenadaController.class, properties = "jwt.secret=" + JwtTestSupport.SECRET)
@Import({SecurityConfig.class, JwtFilter.class})
class CoordenadaControllerTest {

    private static final String BEARER = "Bearer " + JwtTestSupport.validToken();

    @Autowired private MockMvc mockMvc;
    @MockitoBean private CoordenadaService coordenadaService;

    private CoordenadaDTO dto() {
        return CoordenadaDTO.builder()
                .idUbicacionCoordenadas(1L)
                .ubicacionLat(-33.045)
                .ubicacionLon(-71.610)
                .idReporte(10L)
                .idComuna(2L)
                .direccion("Av. Principal 123")
                .build();
    }

    @Test
    void sinToken_debeResponder403() throws Exception {
        mockMvc.perform(get("/api/coordenadas"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(coordenadaService);
    }

    @Test
    void conTokenInvalido_debeResponder401() throws Exception {
        mockMvc.perform(get("/api/coordenadas").header("Authorization", "Bearer basura"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido o expirado"));
    }

    @Test
    void findAll_conToken_debeResponder200() throws Exception {
        when(coordenadaService.findAll()).thenReturn(List.of(dto()));

        mockMvc.perform(get("/api/coordenadas").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReporte").value(10))
                .andExpect(jsonPath("$[0].ubicacionLat").value(-33.045));
    }

    @Test
    void findById_inexistente_debeResponder404ConMensaje() throws Exception {
        when(coordenadaService.findById(99L)).thenThrow(new ResourceNotFoundException("Coordenada no encontrada con id: 99"));

        mockMvc.perform(get("/api/coordenadas/99").header("Authorization", BEARER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Coordenada no encontrada con id: 99"));
    }

    @Test
    void findByReporte_debeResponder200() throws Exception {
        when(coordenadaService.findByReporte(10L)).thenReturn(dto());

        mockMvc.perform(get("/api/coordenadas/reporte/10").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idComuna").value(2));
    }

    @Test
    void findByComuna_debeResponder200() throws Exception {
        when(coordenadaService.findByComuna(2L)).thenReturn(List.of(dto()));

        mockMvc.perform(get("/api/coordenadas/comuna/2").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void save_valido_debeResponder201() throws Exception {
        when(coordenadaService.save(any())).thenReturn(dto());

        mockMvc.perform(post("/api/coordenadas")
                        .header("Authorization", BEARER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ubicacionLat": -33.045, "ubicacionLon": -71.610, "idReporte": 10, "idComuna": 2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUbicacionCoordenadas").value(1));
    }

    @Test
    void save_sinCamposObligatorios_debeResponder400ConDetalle() throws Exception {
        mockMvc.perform(post("/api/coordenadas")
                        .header("Authorization", BEARER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ubicacionLat").value("La latitud es obligatoria"))
                .andExpect(jsonPath("$.ubicacionLon").value("La longitud es obligatoria"))
                .andExpect(jsonPath("$.idReporte").value("El id del reporte es obligatorio"))
                .andExpect(jsonPath("$.idComuna").value("El id de la comuna es obligatorio"));
        verifyNoInteractions(coordenadaService);
    }

    @Test
    void update_debeResponder200() throws Exception {
        when(coordenadaService.update(eq(1L), any())).thenReturn(dto());

        mockMvc.perform(put("/api/coordenadas/1")
                        .header("Authorization", BEARER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ubicacionLat": -33.045, "ubicacionLon": -71.610, "idReporte": 10, "idComuna": 2}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void delete_debeResponder204() throws Exception {
        mockMvc.perform(delete("/api/coordenadas/1").header("Authorization", BEARER))
                .andExpect(status().isNoContent());
        verify(coordenadaService).delete(1L);
    }
}
