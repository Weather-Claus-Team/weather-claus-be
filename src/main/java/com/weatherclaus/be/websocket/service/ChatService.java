package com.weatherclaus.be.websocket.service;

import com.weatherclaus.be.websocket.entity.ChatMessage;
import com.weatherclaus.be.websocket.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public void saveMessage(String username, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(username);
        chatMessage.setMessage(message);
        chatMessageRepository.save(chatMessage);  // DB에 메시지 저장
    }
}
