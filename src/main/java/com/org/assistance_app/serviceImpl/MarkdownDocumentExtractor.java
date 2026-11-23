
package com.org.assistance_app.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.service.DocumentExtractor;

@Component
public class MarkdownDocumentExtractor implements DocumentExtractor {

   @Override
    public boolean supports(String contentType) {
        return "text/markdown".equals(contentType);
    }

    @Override
    public List<Document> extract(MultipartFile file) {
        ByteArrayResource resource;
       try {
           resource = createResource(file);
       } catch (IOException ex) {
           System.getLogger(DocxDocumentExtractor.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
           return null;
        }

        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);

        return reader.get();
    }

    private ByteArrayResource createResource(MultipartFile file)
            throws IOException {

        String filename = file.getOriginalFilename();

        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}



