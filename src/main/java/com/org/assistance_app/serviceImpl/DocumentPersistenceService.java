package com.org.assistance_app.serviceImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.org.assistance_app.document.DocumentStorageFactory;
import com.org.assistance_app.entity.DocumentData;
import com.org.assistance_app.entity.DocumentStatus;
import com.org.assistance_app.exception.DocumentException;
import com.org.assistance_app.repository.DocumentRepository;
import com.org.assistance_app.service.DocumentStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentPersistenceService {

    private final DocumentStorageFactory storageFactory;
    private final DocumentRepository documentRepository;

    /**
     * Upload document asynchronously.
     */
    @Async("documentUploadExecutor")
    public CompletableFuture<Void> upload(
            Long documentId,
            String storageType,
            String storageKey,
            byte[] content,
            String contentType) {

        DocumentData document = getDocument(documentId);

        try (InputStream inputStream = new ByteArrayInputStream(content)) {

            DocumentStorageService storage
                    = storageFactory.getStorage(storageType);

            storage.store(
                    storageKey,
                    inputStream,
                    content.length,
                    contentType
            );

            document.setStatus(DocumentStatus.UPLOADED);
            //document.setFailureReason(null);

            documentRepository.save(document);

            log.info("Document uploaded successfully. ID: {}", documentId);

            return CompletableFuture.completedFuture(null);

        } catch (Exception ex) {

            document.setStatus(DocumentStatus.UPLOAD_FAILED);
            //document.setFailureReason("Document upload failed");

            documentRepository.save(document);

            log.error("Document upload failed. ID: {}", documentId, ex);

            return CompletableFuture.failedFuture(ex);
        }
    }

    /**
     * Download document.
     */
    public InputStream download(Long documentId) {

        DocumentData document = getDocument(documentId);

        if (document.getStatus() != DocumentStatus.UPLOADED) {
            throw new DocumentException(
                    "Document is not available for download",
                    HttpStatus.CONFLICT
            );
        }

        try {
            DocumentStorageService storage
                    = storageFactory.getStorage(document.getStorageType());

            return storage.load(document.getStorageKey());

        } catch (Exception ex) {
            log.error("Document download failed. ID: {}", documentId, ex);

            throw new DocumentException(
                    "Failed to download document",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex
            );
        }
    }

    /**
     * Delete document asynchronously.
     */
    @Async("documentUploadExecutor")
    public CompletableFuture<Void> delete(Long documentId) {

        DocumentData document = getDocument(documentId);

        try {
            DocumentStorageService storage
                    = storageFactory.getStorage(document.getStorageType());

            storage.delete(document.getStorageKey());

            documentRepository.delete(document);

            log.info("Document deleted successfully. ID: {}", documentId);

            return CompletableFuture.completedFuture(null);

        } catch (Exception ex) {
            log.error("Document deletion failed. ID: {}", documentId, ex);

            return CompletableFuture.failedFuture(ex);
        }
    }

    /**
     * Check whether document exists in storage.
     */
    public boolean exists(Long documentId) {

        DocumentData document = getDocument(documentId);

        DocumentStorageService storage
                = storageFactory.getStorage(document.getStorageType());

        return storage.exists(document.getStorageKey());
    }

    /**
     * Retrieve document metadata.
     */
    private DocumentData getDocument(Long documentId) {

        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException(
                        "Document not found: " + documentId,
                        HttpStatus.NOT_FOUND
                ));
    }

    /**
     * Retrieve document metadata.
     */
    public List<DocumentData> getAllDocuments() {

        return documentRepository.findAll();
    }
}
