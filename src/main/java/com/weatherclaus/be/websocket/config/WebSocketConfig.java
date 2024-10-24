package com.weatherclaus.be.websocket.config;

import com.weatherclaus.be.jwt.JWTUtil;
import com.weatherclaus.be.websocket.service.ChatService;
import com.weatherclaus.be.websocket.controller.MyWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final JWTUtil jwtUtil;
    private final ChatService chatService;

    public WebSocketConfig(JWTUtil jwtUtil,ChatService chatService) {
        this.jwtUtil = jwtUtil;
        this.chatService = chatService;
    }




    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(chatService), "/ws")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil)) // JWT 인증 인터셉터
                .setAllowedOrigins("*");
    }
}
