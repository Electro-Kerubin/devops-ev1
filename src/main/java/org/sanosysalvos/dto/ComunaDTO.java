package org.sanosysalvos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComunaDTO {
    private Long idComuna;

    @NotBlank(message = "El nombre de la comuna es obligatorio")
    private String nombreComuna;

    @NotNull(message = "El id de la región es obligatorio")
    private Long idRegion;
}

