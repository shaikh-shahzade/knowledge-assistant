package com.org.assistance_app.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentExtractionService {

    List<Document> extract(MultipartFile file);

}
