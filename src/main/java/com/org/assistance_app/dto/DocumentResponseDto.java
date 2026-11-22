package com.org.assistance_app.dto;

public record DocumentResponseDto(
        Long id,
        String filename,
        String fileType,
        Long fileSize,
        int extractedDocuments,
        int totalChunks,
        String status,
        String message) {
}