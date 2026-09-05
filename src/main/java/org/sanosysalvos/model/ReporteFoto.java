package org.sanosysalvos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_foto")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReporteFoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Long idFoto;

    @Column(name = "id_reporte")
    private Long idReporte;

    @Column(name = "url")
    private String url;

    @Column(name = "key_s3")
    private String keyS3;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}