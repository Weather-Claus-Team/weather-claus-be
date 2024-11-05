package com.weatherclaus.be.websocket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherclaus.be.jwt.JWTUtil;
import com.weatherclaus.be.websocket.dto.ChatListResponse;
import com.weatherclaus.be.websocket.service.ChatService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper;

    private final ChatService chatService;
    private final JWTUtil jwtUtil;

    public MyWebSocketHandler(ChatService chatService
            , ObjectMapper objectMapper,JWTUtil jwtUtil
    ) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        CLIENTS.put(session.getId(), session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        CLIENTS.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {


        String senderId = session.getId();  // 메시지를 보낸 세션의 ID

        String username = (String) session.getAttributes().get("username");

        // 비회원 사용자인 경우 예외를 던져서 메시지 전송을 차단
        if ("guest".equals(username)) {

            // 프론트엔드에서 확인할 수 있는 JSON 형식의 로그인 요청 메시지
            String loginRequiredMessage = objectMapper.writeValueAsString(Map.of(
                    "type", "LOGIN_REQUIRED",
                    "message", "Login required to send messages."
            ));

            session.sendMessage(new TextMessage(loginRequiredMessage));
            return;  // 이후 메시지 전송 로직은 건너뜀

//            log.warn("Guest user attempted to send a message. Prompting for login.");
//            session.sendMessage(new TextMessage("Error: Login required to send messages."));
//            session.close(CloseStatus.NORMAL.withReason("Login required"));
//            throw new IllegalStateException("Login required to send messages.");
        }



        // 메시지 페이로드를 JSON으로 재구성하여 닉네임과 메시지를 포함
        String payload = message.getPayload();

        // 메시지를 DB에 저장
        ChatListResponse chatListResponse = chatService.saveMessage(username, payload);
        String jsonMessage = objectMapper.writeValueAsString(chatListResponse);

        // 모든 세션에 메시지를 전송, 단 현재 세션에게만 보내지 않도록 설정
        CLIENTS.forEach((sessionId, webSocketSession) -> {
            if (!sessionId.equals(senderId)) {  // 자신이 아닌 다른 클라이언트에게만 전송
                try {
                    webSocketSession.sendMessage(new TextMessage(jsonMessage));  // 메시지 전송
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}