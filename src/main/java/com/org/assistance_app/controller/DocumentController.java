package com.org.assistance_app.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.assistance_app.dto.DocumentResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("document")
@CrossOrigin ("*")
public class DocumentController {
    
    @PostMapping("upload")
    public DocumentResponseDto  postMethodName(@RequestBody String document) {
        return entity;
    }
    
}
