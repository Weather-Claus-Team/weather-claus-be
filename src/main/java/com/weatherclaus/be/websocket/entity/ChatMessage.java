package com.weatherclaus.be.websocket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weatherclaus.be.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

//    유저와 lazy-loading 단방향

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatmessage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sentDate;


    @Builder
    public ChatMessage(User user, String message, LocalDateTime sentDate) {
        this.user = user;
        this.message = message;
        this.sentDate = sentDate;
    }
}