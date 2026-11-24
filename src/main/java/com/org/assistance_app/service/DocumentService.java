package com.org.assistance_app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.dto.DocumentResponseDto;

public interface DocumentService {

    DocumentResponseDto processDocument(MultipartFile file);

    List<DocumentResponseDto> getAllDocuments();

    void deleteDocument(Long documentId);

    DocumentResponseDto generateDownloadURL(Long documentId);

}

