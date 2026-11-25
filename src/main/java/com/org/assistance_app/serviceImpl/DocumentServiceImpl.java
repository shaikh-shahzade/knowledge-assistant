package com.org.assistance_app.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.document.DocumentProcessingServiceImpl;
import com.org.assistance_app.entity.DocumentData;
import com.org.assistance_app.entity.DocumentStatus;
import com.org.assistance_app.repository.DocumentRepository;
import com.org.assistance_app.service.DocumentService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentPersistenceService documentPersistenceService;

    private final DocumentProcessingServiceImpl documentProcessingService;

    private final DocumentRepository documentRepository;

    @Override
    public DocumentData processDocument(MultipartFile file) {

        //  1. Validate File
        validateFile(file);

        String filename = file.getOriginalFilename();
        String extension = getExtension(filename);

        // Step 2: Create and persist document record
        DocumentData uploadedDocument
                = DocumentData.builder()
                        .filename(filename)
                        .fileType(extension)
                        .contentType(file.getContentType())
                        .fileSize(file.getSize())
                        .status(DocumentStatus.PROCESSING)
                        .extractedDocuments(0)
                        .totalChunks(0)
                        .build();

        uploadedDocument = documentRepository.save(uploadedDocument);

        // 3. upload Document to any storage Type
        String storageKey = filename + UUID.randomUUID();
        String storageType = "local";

        try {
            documentPersistenceService.upload(uploadedDocument.getId(), storageType, storageKey, file.getBytes(), extension);
        } catch (IOException ex) {

        }

        // Move
        return documentProcessingService.processDocument(file, uploadedDocument);
    }

    @Override
    public List<DocumentData> getAllDocuments() {
        return documentPersistenceService.getAllDocuments();
    }

    @Override
    public void deleteDocument(Long documentId) {
        documentPersistenceService.delete(documentId);
    }

    @Override
    public InputStream generateDownloadURL(Long documentId) {
        return documentPersistenceService.download(documentId);
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please upload a valid document"
            );
        }

        if (file.getSize() > 20 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Maximum file size allowed is 20 MB"
            );
        }

        String filename = file.getOriginalFilename();

        if (filename == null || !filename.matches(
                "(?i)^.+\\.(pdf|txt|md|docx)$")) {

            throw new IllegalArgumentException(
                    "Supported formats: PDF, TXT, MD, DOCX"
            );
        }
    }

    private String getExtension(String filename) {

        return filename.substring(filename.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ROOT);
    }

}
