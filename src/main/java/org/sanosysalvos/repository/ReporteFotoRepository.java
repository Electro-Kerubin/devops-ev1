package org.sanosysalvos.repository;

import org.sanosysalvos.model.ReporteFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteFotoRepository extends JpaRepository<ReporteFoto, Long> {
    List<ReporteFoto> findByIdReporteOrderByCreatedAtAsc(Long idReporte);
}