-- ============================================
-- Script de inicialización: BBDD Geolocalización
-- ============================================

CREATE TABLE IF NOT EXISTS region (
    id_region    BIGSERIAL PRIMARY KEY,
    nombre_region VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS comuna (
    id_comuna     BIGSERIAL PRIMARY KEY,
    nombre_comuna VARCHAR(100) NOT NULL,
    id_region     BIGINT NOT NULL,
    CONSTRAINT fk_comuna_region FOREIGN KEY (id_region) REFERENCES region(id_region)
);

CREATE TABLE IF NOT EXISTS coordenadas (
    id_ubicacion_coordenadas BIGSERIAL PRIMARY KEY,
    ubicacion_lat            DOUBLE PRECISION NOT NULL,
    ubicacion_lon            DOUBLE PRECISION NOT NULL,
    id_reporte               BIGINT NOT NULL,
    id_comuna                BIGINT NOT NULL,
    direccion                VARCHAR(255),
    created_at               TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_coordenadas_comuna FOREIGN KEY (id_comuna) REFERENCES comuna(id_comuna)
);

CREATE TABLE IF NOT EXISTS mapadecalor (
    id_mapadecalor     BIGSERIAL PRIMARY KEY,
    geohash            VARCHAR(20) NOT NULL,
    cantidad_reportes  INT NOT NULL DEFAULT 0,
    last_calculated_at TIMESTAMP
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_coordenadas_reporte   ON coordenadas(id_reporte);
CREATE INDEX IF NOT EXISTS idx_coordenadas_comuna    ON coordenadas(id_comuna);
CREATE INDEX IF NOT EXISTS idx_mapadecalor_geohash   ON mapadecalor(geohash);
CREATE INDEX IF NOT EXISTS idx_comuna_region         ON comuna(id_region);

