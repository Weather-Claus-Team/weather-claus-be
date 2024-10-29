package com.weatherclaus.be.websocket.service;

import com.weatherclaus.be.user.entity.User;
import com.weatherclaus.be.user.service.UserService;
import com.weatherclaus.be.websocket.dto.ChatListResponse;
import com.weatherclaus.be.websocket.entity.ChatMessage;
import com.weatherclaus.be.websocket.repository.ChatMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;

    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository, UserService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
    }



    public ChatListResponse saveMessage(String username, String message) {


        User user = userService.findByUsername(username);

        ChatMessage chatMessage = ChatMessage.builder()
                                    .user(user)
                                    .message(message)
                                    .sentDate(LocalDateTime.now())
                                    .build();

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);// DB에 메시지 저장


        return new ChatListResponse(savedChatMessage.getMessage(), savedChatMessage.getSentDate(), savedChatMessage.getUser().getNickname(), savedChatMessage.getUser().getImageUrl());
    }





    public Slice<ChatListResponse> chatList(Pageable pageable) {

        return chatMessageRepository.findAllChatMessages(pageable).map(c -> new ChatListResponse(c.getMessage(), c.getSentDate(), c.getUser().getNickname(), c.getUser().getImageUrl()));

    }

}
