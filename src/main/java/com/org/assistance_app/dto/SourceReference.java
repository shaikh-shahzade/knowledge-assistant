package com.org.assistance_app.dto;

public record SourceReference(
        Long documentId,
        String filename,
        Integer pageNumber,
        Integer chunkIndex
) {}