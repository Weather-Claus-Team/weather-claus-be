package com.weatherclaus.be.websocket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherclaus.be.websocket.dto.ChatMessageRequest;
import com.weatherclaus.be.websocket.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ChatService chatService;

    public MyWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        CLIENTS.put(session.getId(), session);

        String username = (String) session.getAttributes().get("username");
        System.out.println("User " + username + " connected.");
        // 연결 성공 시, 특정 사용자 정보를 기반으로 세션을 관리
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



        // SecurityContext에서 인증 정보 가져오기 (사용자가 보낸 메시지 정보 확인)
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = (String) authentication.getPrincipal();  // 또는 session.getAttributes().get("username") 사용 가능

        String username = (String) session.getAttributes().get("username");

        // SecurityContext에 Authentication이 없으면 다시 설정
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        username = (String) authentication.getPrincipal();  // 또는 session.getAttributes().get("username") 사용 가능

        // 메시지 페이로드를 JSON으로 재구성하여 닉네임과 메시지를 포함
        String payload = message.getPayload();
        ChatMessageRequest chatMessage = new ChatMessageRequest(username, payload);
        String jsonMessage = objectMapper.writeValueAsString(chatMessage);

        // 메시지를 DB에 저장
        chatService.saveMessage(username, payload);

        // 모든 세션에 메시지를 전송, 단 현재 세션에게만 보내지 않도록 설정
        CLIENTS.forEach((sessionId, webSocketSession) -> {
            if (!sessionId.equals(senderId)) {  // 자신이 아닌 다른 클라이언트에게만 전송
                try {
                    webSocketSession.sendMessage(message);
//                    webSocketSession.sendMessage(new TextMessage(jsonMessage));  // 메시지 전송
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}