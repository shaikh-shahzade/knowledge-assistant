package com.org.assistance_app.dto;

public record SourceReference(
        String documentId,
        String filename,
        Integer pageNumber,
        Integer chunkIndex
) {}