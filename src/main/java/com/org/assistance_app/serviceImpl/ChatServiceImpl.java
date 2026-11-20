package com.org.assistance_app.serviceImpl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.org.assistance_app.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

    private ChatClient chatClient;

    ChatServiceImpl(ChatClient.Builder cBuilder) {
        chatClient = cBuilder.build();
    }

    public String chat(String query) {
        return chatClient.prompt(query).call().content();
    }
}
