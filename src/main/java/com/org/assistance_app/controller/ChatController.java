package com.org.assistance_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.assistance_app.service.ChatService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping ("api/chat")
public class ChatController {

    private ChatService chatService;

    ChatController(ChatService chatService)
    {
        this.chatService = chatService;
    }
    
    @GetMapping("path")
    public String getMethodName(@RequestParam String query) {
        return chatService.chat(query);
    }
    
}
