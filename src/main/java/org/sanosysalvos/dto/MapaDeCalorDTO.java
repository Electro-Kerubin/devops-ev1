package org.sanosysalvos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MapaDeCalorDTO {
    private Long idMapaDeCalor;

    @NotBlank(message = "El geohash es obligatorio")
    private String geohash;

    @NotNull(message = "La cantidad de reportes es obligatoria")
    private Integer cantidadReportes;

    private LocalDateTime lastCalculatedAt;
}

