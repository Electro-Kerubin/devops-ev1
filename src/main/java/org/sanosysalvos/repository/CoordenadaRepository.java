package org.sanosysalvos.repository;

import org.sanosysalvos.entity.Coordenada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoordenadaRepository extends JpaRepository<Coordenada, Long> {
    List<Coordenada> findByComuna_IdComuna(Long idComuna);
    Optional<Coordenada> findByIdReporte(Long idReporte);
    List<Coordenada> findByUbicacionLatBetweenAndUbicacionLonBetween(
            Double latMin, Double latMax, Double lonMin, Double lonMax);
}

