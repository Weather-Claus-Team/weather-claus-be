package com.weatherclaus.be.websocket.repository;

import com.weatherclaus.be.user.entity.User;
import com.weatherclaus.be.websocket.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageCustom {


    @Modifying
    @Query("DELETE FROM ChatMessage c WHERE c.user.id = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}
