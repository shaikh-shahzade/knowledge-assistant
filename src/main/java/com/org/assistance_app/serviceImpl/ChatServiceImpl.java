package com.org.assistance_app.serviceImpl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.org.assistance_app.dto.ChatRequest;
import com.org.assistance_app.dto.ChatResponse;
import com.org.assistance_app.dto.SearchRequestDto;
import com.org.assistance_app.dto.SearchResultDto;
import com.org.assistance_app.service.ChatService;

import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService {

    private ChatClient chatClient;

    ChatServiceImpl(ChatClient.Builder cBuilder) {
        chatClient = cBuilder.build();
    }
    
    @Override
    public ChatResponse chat(String query) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chat'");
    }

    @Override
    public ChatResponse chat(ChatRequest requestDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chat'");
    }

    @Override
    public Flux<String> streamChat(ChatRequest requestDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'streamChat'");
    }

    @Override
    public SearchResultDto searchSimilar(SearchRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchSimilar'");
    }


    
}
