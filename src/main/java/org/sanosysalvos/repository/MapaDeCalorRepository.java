package org.sanosysalvos.repository;

import org.sanosysalvos.entity.MapaDeCalor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapaDeCalorRepository extends JpaRepository<MapaDeCalor, Long> {
    Optional<MapaDeCalor> findByGeohash(String geohash);
    List<MapaDeCalor> findAllByOrderByCantidadReportesDesc();
}

