package com.org.assistance_app.document;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.org.assistance_app.exception.DocumentException;
import com.org.assistance_app.service.DocumentExtractor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentExtractorFactory {

    private final List<DocumentExtractor> extractors;

    public DocumentExtractor getExtractor(String contentType) {

        return extractors.stream()
                .filter(extractor -> extractor.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new DocumentException(
                        "Unsupported document format: " + contentType,
                        HttpStatus.BAD_REQUEST
                ));
    }
}