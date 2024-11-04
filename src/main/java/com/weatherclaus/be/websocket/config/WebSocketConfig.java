package com.weatherclaus.be.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherclaus.be.jwt.JWTUtil;
import com.weatherclaus.be.user.service.UserService;
import com.weatherclaus.be.websocket.controller.MyWebSocketHandler;
import com.weatherclaus.be.websocket.service.ChatService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    private final JWTUtil jwtUtil;
    private final ChatService chatService;
    private final ObjectMapper webSocketObjectMapper;

    public WebSocketConfig(JWTUtil jwtUtil,ChatService chatService
            , ObjectMapper webSocketObjectMapper) {
        this.jwtUtil = jwtUtil;
        this.chatService = chatService;
        this.webSocketObjectMapper = webSocketObjectMapper;
    }




    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(chatService
                        , webSocketObjectMapper
                ), "/ws")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil)) // JWT 인증 인터셉터
                .setAllowedOrigins("*")
                .withSockJS();  // SockJS 설정 추가

    }
}
