package com.org.assistance_app.service;

import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.dto.DocumentResponseDto;

public interface DocumentService {

    DocumentResponseDto processDocument(MultipartFile file);

}

