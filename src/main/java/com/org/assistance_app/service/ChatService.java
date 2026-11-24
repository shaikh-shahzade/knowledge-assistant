package com.org.assistance_app.service;

import com.org.assistance_app.dto.ChatRequest;
import com.org.assistance_app.dto.ChatResponse;
import com.org.assistance_app.dto.SearchRequestDto;
import com.org.assistance_app.dto.SearchResultDto;

import reactor.core.publisher.Flux;

public interface ChatService {

    public ChatResponse chat(ChatRequest requestDto);

    public ChatResponse chat(String query);

    public Flux<String> streamChat(ChatRequest requestDto);

    public SearchResultDto searchSimilar(SearchRequestDto request);
} 