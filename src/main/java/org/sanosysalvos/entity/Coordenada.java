package org.sanosysalvos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coordenadas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coordenada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion_coordenadas")
    private Long idUbicacionCoordenadas;

    @Column(name = "ubicacion_lat", nullable = false)
    private Double ubicacionLat;

    @Column(name = "ubicacion_lon", nullable = false)
    private Double ubicacionLon;

    @Column(name = "id_reporte", nullable = false)
    private Long idReporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comuna", nullable = false)
    private Comuna comuna;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

