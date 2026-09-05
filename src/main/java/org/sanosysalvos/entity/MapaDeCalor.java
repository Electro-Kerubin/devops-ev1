package org.sanosysalvos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mapadecalor")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MapaDeCalor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mapadecalor")
    private Long idMapaDeCalor;

    @Column(name = "geohash", nullable = false, length = 20)
    private String geohash;

    @Column(name = "cantidad_reportes", nullable = false)
    private Integer cantidadReportes;

    @Column(name = "last_calculated_at")
    private LocalDateTime lastCalculatedAt;
}

