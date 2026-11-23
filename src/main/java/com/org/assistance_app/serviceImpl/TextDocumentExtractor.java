package com.org.assistance_app.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.exception.DocumentException;
import com.org.assistance_app.service.DocumentExtractor;

@Component
public class TextDocumentExtractor implements DocumentExtractor {

   @Override
    public boolean supports(String contentType) {
        return MediaType.TEXT_PLAIN_VALUE.equals(contentType);
    }

    @Override
    public List<Document> extract(MultipartFile file) {
        try {
            String content = new String(
                    file.getBytes(),
                    StandardCharsets.UTF_8
            );

            return List.of(new Document(content));
        } catch (Exception ex) {
            throw new DocumentException(
                    "Failed to extract text document",
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    ex
            );
        }
    }
    
}
