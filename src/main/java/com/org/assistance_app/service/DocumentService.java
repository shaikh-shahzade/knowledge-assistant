package com.org.assistance_app.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.entity.DocumentData;


public interface DocumentService {

    DocumentData processDocument(MultipartFile file);

    List<DocumentData> getAllDocuments();

    void deleteDocument(Long documentId);

    InputStream generateDownloadURL(Long documentId);
    

}

