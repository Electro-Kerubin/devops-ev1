package org.sanosysalvos.service;

import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.UploadResponseDto;
import org.sanosysalvos.model.ReporteFoto;
import org.sanosysalvos.repository.ReporteFotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3Client;
    private final ReporteFotoRepository fotoRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    public UploadResponseDto uploadFile(MultipartFile file, Long idReporte) throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        String key = String.format("reportes/%d/%s%s", idReporte, UUID.randomUUID(), extension);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);

        // Guardar en BD
        fotoRepository.save(ReporteFoto.builder()
                .idReporte(idReporte)
                .url(url)
                .keyS3(key)
                .createdAt(LocalDateTime.now())
                .build());

        return new UploadResponseDto(url, key, idReporte);
    }

    public List<String> getFotosByReporte(Long idReporte) {
        return fotoRepository.findByIdReporteOrderByCreatedAtAsc(idReporte)
                .stream()
                .map(ReporteFoto::getUrl)
                .toList();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}