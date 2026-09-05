package org.sanosysalvos.dto.geojson;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Representa una Feature GeoJSON con geometría Point.
 * Compatible con Leaflet.js (L.geoJSON) y OpenLayers (ol/format/GeoJSON).
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoJsonFeature {

    private final String type = "Feature";
    private GeoJsonGeometry geometry;
    private Map<String, Object> properties;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GeoJsonGeometry {
        private final String type = "Point";
        /** GeoJSON usa [longitud, latitud] */
        private List<Double> coordinates;
    }
}

