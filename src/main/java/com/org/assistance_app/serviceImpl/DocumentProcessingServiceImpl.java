package com.org.assistance_app.serviceImpl;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.dto.DocumentResponseDto;
import com.org.assistance_app.entity.DocumentData;
import com.org.assistance_app.entity.DocumentStatus;
import com.org.assistance_app.repository.DocumentRepository;
import com.org.assistance_app.service.DocumentExtractionService;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class DocumentProcessingServiceImpl {

    private final DocumentExtractionService extractionService;
    private final TokenTextSplitter textSplitter;
    private final VectorStore vectorStore;
    private final DocumentRepository documentRepository;

    public DocumentProcessingServiceImpl(
            DocumentExtractionService extractionService,
            TokenTextSplitter textSplitter,
            VectorStore vectorStore,
            DocumentRepository documentRepository) {

        this.extractionService = extractionService;
        this.textSplitter = textSplitter;
        this.vectorStore = vectorStore;
        this.documentRepository = documentRepository;
    }

    public DocumentResponseDto uploadDocument(MultipartFile file) {

        validateFile(file);

        String filename = file.getOriginalFilename();
        String extension = getExtension(filename);


        // Step 2: Create and persist document record
        DocumentData uploadedDocument = 
        DocumentData.builder()
        .filename(filename)
        .fileType(extension)
        .contentType(file.getContentType())
        .fileSize(file.getSize())
        .status(DocumentStatus.PROCESSING)
        .extractedDocuments(0)
        .totalChunks(0)
        .build();

        uploadedDocument = documentRepository.save(uploadedDocument);

        Long documentId = uploadedDocument.getId();

        try {

            // Step 3: Extract document content
            List<Document> extractedDocuments =
                extractionService.extract(file);

            if (extractedDocuments.isEmpty()) {
                throw new IllegalArgumentException(
                    "No content could be extracted"
                );
            }

            // Step 4: Attach document metadata
            extractedDocuments.forEach(document -> {
                document.getMetadata().put(
                    "documentId", documentId.toString()
                );
                document.getMetadata().put("filename", filename);
                document.getMetadata().put("fileType", extension);
            });

            // Step 5: Generate chunks
            List<Document> chunks =
                textSplitter.split(extractedDocuments);

            if (chunks.isEmpty()) {
                throw new IllegalArgumentException(
                    "Document contains no indexable content"
                );
            }

            // Step 6: Attach chunk metadata
            for (int i = 0; i < chunks.size(); i++) {

                Document chunk = chunks.get(i);

                chunk.getMetadata().put(
                    "documentId", documentId.toString()
                );

                chunk.getMetadata().put("filename", filename);
                chunk.getMetadata().put("fileType", extension);
                chunk.getMetadata().put("chunkIndex", i);
                chunk.getMetadata().put("totalChunks", chunks.size());
            }

            // Step 7: Generate embeddings and save to PGVector
            vectorStore.add(chunks);

            // Step 8: Update relational document record
            uploadedDocument.setExtractedDocuments(
                extractedDocuments.size()
            );

            uploadedDocument.setTotalChunks(chunks.size());
            uploadedDocument.setStatus(DocumentStatus.COMPLETED);

            uploadedDocument =
                documentRepository.save(uploadedDocument);

            return new DocumentResponseDto(
                documentId,
                filename,
                extension,
                file.getSize(),
                extractedDocuments.size(),
                chunks.size(),
                uploadedDocument.getStatus().name(),
                "Document successfully processed and indexed"
            );

        } catch (Exception exception) {

            // Step 9: Mark document processing as failed
            uploadedDocument.setStatus(DocumentStatus.FAILED);
            uploadedDocument.setErrorMessage(
                truncateError(exception.getMessage())
            );

            documentRepository.save(uploadedDocument);

            throw new RuntimeException(
                "Document ingestion failed for: " + filename,
                exception
            );
        }
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

    private String truncateError(String message) {

        if (message == null) {
            return "Unknown processing error";
        }

        return message.length() > 2000
            ? message.substring(0, 2000)
            : message;
    }
}

