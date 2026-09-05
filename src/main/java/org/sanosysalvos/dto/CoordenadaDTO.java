package org.sanosysalvos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CoordenadaDTO {
    private Long idUbicacionCoordenadas;

    @NotNull(message = "La latitud es obligatoria")
    private Double ubicacionLat;

    @NotNull(message = "La longitud es obligatoria")
    private Double ubicacionLon;

    @NotNull(message = "El id del reporte es obligatorio")
    private Long idReporte;

    @NotNull(message = "El id de la comuna es obligatorio")
    private Long idComuna;

    private String direccion;

    private LocalDateTime createdAt;

    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
    private Double ubicacionLat;

    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
    private Double ubicacionLon;
}

