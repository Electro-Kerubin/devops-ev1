package org.sanosysalvos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegionDTO {
    private Long idRegion;

    @NotBlank(message = "El nombre de la región es obligatorio")
    private String nombreRegion;
}

