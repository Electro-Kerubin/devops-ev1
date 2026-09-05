package org.sanosysalvos.dto.geojson;

import lombok.*;

import java.util.List;

/**
 * Representa un FeatureCollection GeoJSON.
 * Compatible con Leaflet.js (L.geoJSON) y OpenLayers (ol/format/GeoJSON).
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeoJsonFeatureCollection {

    private final String type = "FeatureCollection";
    private List<GeoJsonFeature> features;
}

