package com.org.assistance_app.controller;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.dto.DocumentResponseDto;
import com.org.assistance_app.service.DocumentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("document")
@CrossOrigin("*")
public class DocumentController {

    private DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

     @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @RequestParam("file") MultipartFile file) {

        DocumentResponseDto response = documentService.processDocument(file);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Get all documents.
     */
    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> getAllDocuments() {

        List<DocumentResponseDto> documents =
                documentService.getAllDocuments();

        return ResponseEntity.ok(documents);
    }

    /**
     * Download original document.
     */
    @GetMapping("/{documentId}/download")
    public ResponseEntity<DocumentResponseDto> downloadDocument(
            @PathVariable Long documentId) {

        DocumentResponseDto document =
                documentService.generateDownloadURL(documentId);

        return ResponseEntity.ok()
                .body(document);
    }

    /**
     * Delete document and associated vectors.
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long documentId) {

        documentService.deleteDocument(documentId);

        return ResponseEntity.accepted().build();
    }

}
