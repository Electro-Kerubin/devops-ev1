package org.sanosysalvos.dto.geojson;

import lombok.*;

/**
 * Punto de mapa de calor listo para Leaflet.heat o ol-ext heatmap.
 * Incluye lat/lon ya calculados para no requerir decodificación de geohash en el frontend.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HeatmapPointDTO {
    private String geohash;
    private Double lat;
    private Double lon;
    private Integer cantidadReportes;
}

