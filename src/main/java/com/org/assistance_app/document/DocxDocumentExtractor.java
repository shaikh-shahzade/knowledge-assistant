package com.org.assistance_app.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.service.DocumentExtractor;

@Component
public class DocxDocumentExtractor implements DocumentExtractor {

   @Override
    public boolean supports(String contentType) {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                .equals(contentType);
    }

    @Override
    public List<Document> extract(MultipartFile file) {
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
        } catch (IOException ex) {
           System.getLogger(DocxDocumentExtractor.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
       }
       return null;
    }
    
}

