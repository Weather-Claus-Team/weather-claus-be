package com.weatherclaus.be.websocket.controller;

import com.weatherclaus.be.websocket.dto.ChatListResponse;
import com.weatherclaus.be.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @GetMapping("/api/chatList")
    public Slice<ChatListResponse> chatList(@PageableDefault(page = 0, size = 10) Pageable pageable) {

        Slice<ChatListResponse> chatListResponses = chatService.chatList(pageable);

        return chatListResponses;
    }


}
