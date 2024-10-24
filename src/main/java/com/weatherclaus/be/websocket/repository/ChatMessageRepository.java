package com.weatherclaus.be.websocket.repository;

import com.weatherclaus.be.websocket.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
