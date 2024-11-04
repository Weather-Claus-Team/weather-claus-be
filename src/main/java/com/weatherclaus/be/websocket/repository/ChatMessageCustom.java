package com.weatherclaus.be.websocket.repository;

import com.weatherclaus.be.websocket.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageCustom {

    Slice<ChatMessage> findAllChatMessages(Pageable pageable);
}
