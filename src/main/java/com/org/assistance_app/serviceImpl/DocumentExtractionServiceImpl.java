package com.org.assistance_app.serviceImpl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.service.DocumentExtractionService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
public class DocumentExtractionServiceImpl implements DocumentExtractionService {

    private final DocumentExtractorFactory extractorFactory;

    public DocumentExtractionServiceImpl (DocumentExtractorFactory extractorFactory)
    {
        this.extractorFactory = extractorFactory;
    }

    public List<Document> extract(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename is missing");
        }

        String extension = getFileExtension(filename);

        try {

            DocumentExtractor extractor =
                extractorFactory.getExtractor(file.getContentType());

            List<Document> documents = extractor.extract(file);

            if (documents == null || documents.isEmpty()) {
                throw new IllegalArgumentException(
                        "No content could be extracted from the file");
            }

            // Add common metadata to every extracted document.
            documents.forEach(document -> {
                document.getMetadata().put("filename", filename);
                document.getMetadata().put("fileType", extension);
            });

            return documents.stream()
                    .filter(document -> document.getText() != null)
                    .filter(document -> !document.getText().isBlank())
                    .toList();

        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Failed to read uploaded file: " + filename,
                    exception);
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Document extraction failed for: " + filename,
                    exception);
        }
    }

    private List<Document> extractPdf(MultipartFile file) throws IOException {

        ByteArrayResource resource = createResource(file);

        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);

        return reader.get();
    }

    private List<Document> extractTxt(MultipartFile file) throws IOException {

        ByteArrayResource resource = createResource(file);

        TextReader reader = new TextReader(resource);

        return reader.get();
    }

    private List<Document> extractMarkdown(MultipartFile file)
            throws IOException {

        ByteArrayResource resource = createResource(file);

        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);

        return reader.get();
    }

    private List<Document> extractDocx(MultipartFile file)
            throws IOException {

        try (XWPFDocument wordDocument = new XWPFDocument(
                new ByteArrayInputStream(file.getBytes()))) {

            StringBuilder content = new StringBuilder();

            // Extract paragraphs.
            for (XWPFParagraph paragraph : wordDocument.getParagraphs()) {
                String text = paragraph.getText();

                if (text != null && !text.isBlank()) {
                    content.append(text).append("\n");
                }
            }

            // Extract tables.
            for (XWPFTable table : wordDocument.getTables()) {
                for (XWPFTableRow row : table.getRows()) {

                    for (XWPFTableCell cell : row.getTableCells()) {
                        content.append(cell.getText()).append(" | ");
                    }

                    content.append("\n");
                }
            }

            String extractedText = content.toString().trim();

            if (extractedText.isBlank()) {
                throw new IllegalArgumentException(
                        "No readable text found in DOCX file");
            }

            return List.of(new Document(extractedText));
        }
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

    private String getFileExtension(String filename) {

        int lastDotIndex = filename.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new IllegalArgumentException(
                    "File must have a valid extension");
        }

        return filename.substring(lastDotIndex + 1)
                .toLowerCase(Locale.ROOT);
    }
}
