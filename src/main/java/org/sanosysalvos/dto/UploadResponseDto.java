package org.sanosysalvos.dto;

public record UploadResponseDto(
        String url,
        String key,
        Long idReporte
) {}