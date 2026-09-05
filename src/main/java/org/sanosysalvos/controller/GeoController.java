package org.sanosysalvos.controller;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;
import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.geojson.GeoJsonFeature;
import org.sanosysalvos.dto.geojson.GeoJsonFeature.GeoJsonGeometry;
import org.sanosysalvos.dto.geojson.GeoJsonFeatureCollection;
import org.sanosysalvos.dto.geojson.HeatmapPointDTO;
import org.sanosysalvos.entity.Coordenada;
import org.sanosysalvos.entity.MapaDeCalor;
import org.sanosysalvos.repository.CoordenadaRepository;
import org.sanosysalvos.repository.MapaDeCalorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador que expone datos en formato GeoJSON y Heatmap
 * listos para ser consumidos directamente por Leaflet.js u OpenLayers.
 */
@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {

    private final CoordenadaRepository coordenadaRepository;
    private final MapaDeCalorRepository mapaDeCalorRepository;

    /**
     * Retorna todas las coordenadas como FeatureCollection GeoJSON.
     * Consumo en Leaflet:   L.geoJSON(data).addTo(map)
     * Consumo en OpenLayers: new GeoJSON().readFeatures(data)
     */
    @GetMapping("/coordenadas")
    public ResponseEntity<GeoJsonFeatureCollection> getCoordenadasGeoJson() {
        List<Coordenada> coordenadas = coordenadaRepository.findAll();

        List<GeoJsonFeature> features = coordenadas.stream().map(c -> {
            Map<String, Object> props = new HashMap<>();
            props.put("idUbicacionCoordenadas", c.getIdUbicacionCoordenadas());
            props.put("idReporte", c.getIdReporte());
            props.put("direccion", c.getDireccion());
            props.put("idComuna", c.getComuna().getIdComuna());
            props.put("nombreComuna", c.getComuna().getNombreComuna());
            props.put("createdAt", c.getCreatedAt());

            return GeoJsonFeature.builder()
                    .geometry(GeoJsonGeometry.builder()
                            // GeoJSON: [lon, lat]
                            .coordinates(List.of(c.getUbicacionLon(), c.getUbicacionLat()))
                            .build())
                    .properties(props)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(GeoJsonFeatureCollection.builder().features(features).build());
    }

    /**
     * Retorna coordenadas de una comuna específica como FeatureCollection GeoJSON.
     */
    @GetMapping("/coordenadas/comuna/{idComuna}")
    public ResponseEntity<GeoJsonFeatureCollection> getCoordenadasByComuna(@PathVariable Long idComuna) {
        List<Coordenada> coordenadas = coordenadaRepository.findByComuna_IdComuna(idComuna);

        List<GeoJsonFeature> features = coordenadas.stream().map(c -> {
            Map<String, Object> props = new HashMap<>();
            props.put("idReporte", c.getIdReporte());
            props.put("direccion", c.getDireccion());
            props.put("nombreComuna", c.getComuna().getNombreComuna());

            return GeoJsonFeature.builder()
                    .geometry(GeoJsonGeometry.builder()
                            .coordinates(List.of(c.getUbicacionLon(), c.getUbicacionLat()))
                            .build())
                    .properties(props)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(GeoJsonFeatureCollection.builder().features(features).build());
    }

    /**
     * Retorna puntos del mapa de calor con lat/lon ya resueltos.
     * Consumo en Leaflet (leaflet.heat):
     *   const points = data.map(p => [p.lat, p.lon, p.cantidadReportes]);
     *   L.heatLayer(points, { radius: 25 }).addTo(map);
     * Consumo en OpenLayers (ol-ext Heatmap):
     *   similar usando p.lat, p.lon, p.cantidadReportes
     */
    @GetMapping("/heatmap")
    public ResponseEntity<List<HeatmapPointDTO>> getHeatmap() {
        List<MapaDeCalor> mapas = mapaDeCalorRepository.findAllByOrderByCantidadReportesDesc();

        List<HeatmapPointDTO> points = mapas.stream().map(m -> {
            double lat = 0.0;
            double lon = 0.0;
            try {
                LatLong latLong = GeoHash.decodeHash(m.getGeohash());
                lat = latLong.getLat();
                lon = latLong.getLon();
            } catch (Exception ignored) {
                // geohash inválido: se deja en 0,0
            }
            return HeatmapPointDTO.builder()
                    .geohash(m.getGeohash())
                    .lat(lat)
                    .lon(lon)
                    .cantidadReportes(m.getCantidadReportes())
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(points);
    }
}

