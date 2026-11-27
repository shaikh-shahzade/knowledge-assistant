package com.org.assistance_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.assistance_app.dto.ChatRequest;
import com.org.assistance_app.dto.ChatResponse;
import com.org.assistance_app.dto.SearchRequestDto;
import com.org.assistance_app.dto.SearchResultDto;
import com.org.assistance_app.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;


@RestController 
@RequestMapping ("api/chat")
public class ChatController {

    private final ChatService chatService;

    ChatController(ChatService chatService)
    {
        this.chatService = chatService;
    }
    
    @GetMapping("/chat")
    public ChatResponse getMethodName(@RequestParam String query) {
        return chatService.chat(query);
    }
    

    @PostMapping("/query")
    public ResponseEntity<ChatResponse> askQuestion(
            @Valid @RequestBody ChatRequest requestDto
    ) {

       ChatResponse chatResponseDto = chatService.chat(requestDto);

        return ResponseEntity.ok(
                chatResponseDto
        );

    }

    @PostMapping(value = "/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream real-time Q&A answer tokens via Server-Sent Events (SSE)")
    public Flux<String> streamQuestion(
            @Valid @RequestBody ChatRequest requestDto
    ){
        return chatService.streamChat(requestDto);
    }


    @PostMapping("/search/similarity")
    @Operation(summary = "Perform semantic similarity search on stored document vectors")
    public ResponseEntity<SearchResultDto> searchSimilar(
            @Valid @RequestBody SearchRequestDto request

    ) {

        SearchResultDto results = chatService.searchSimilar(request);
        return ResponseEntity.ok(
                results
        );
    }
}
