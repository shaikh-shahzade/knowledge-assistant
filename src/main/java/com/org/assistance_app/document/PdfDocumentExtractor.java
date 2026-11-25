package com.org.assistance_app.document;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.exception.DocumentException;
import com.org.assistance_app.service.DocumentExtractor;

@Component
public class PdfDocumentExtractor implements DocumentExtractor {

    @Override
    public boolean supports(String contentType) {
        return MediaType.APPLICATION_PDF_VALUE.equals(contentType);
    }

    @Override
    public List<Document> extract(MultipartFile file) {
        try {
            PagePdfDocumentReader reader
                    = new PagePdfDocumentReader(
                            new ByteArrayResource(file.getBytes())
                    );

            return reader.get();
        } catch (Exception ex) {
            throw new DocumentException(
                    "Failed to extract PDF document",
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    ex
            );
        }
    }
}
