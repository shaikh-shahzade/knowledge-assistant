package com.org.assistance_app.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.org.assistance_app.dto.DocumentResponseDto;
import com.org.assistance_app.service.DocumentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("document")
@CrossOrigin ("*")
public class DocumentController {
    
    private DocumentService documentService;
    public DocumentController(DocumentService documentService)
    {
        this.documentService = documentService;
    }

     @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public DocumentResponseDto  postMethodName(@RequestBody MultipartFile file) {
        return documentService.processDocument(file);
    }
    
}
