package com.weatherclaus.be.websocket.service;

import com.weatherclaus.be.user.entity.User;
import com.weatherclaus.be.user.service.ProfileService;
import com.weatherclaus.be.user.service.UserService;
import com.weatherclaus.be.websocket.dto.ChatListResponse;
import com.weatherclaus.be.websocket.entity.ChatMessage;
import com.weatherclaus.be.websocket.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;




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

    public void deleteMessage(User user) {
        chatMessageRepository.deleteByUserId(user.getId());
    }





    public Slice<ChatListResponse> chatList(Pageable pageable) {

//        String loginUserNickname;
//
//
//
//
//        // SecurityContextHolder를 통해 현재 인증 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            // 인증되지 않은 경우 "guest"로 설정
//            loginUserNickname = "guest";
//        } else {
//            // 인증된 경우 사용자 이름 가져오기
//            String username = authentication.getName();
//
//            User user = userService.findByUsername(username);
//
//            loginUserNickname= user.getNickname();
//        }
//
//        log.info("사용자: " + loginUserNickname);


        return chatMessageRepository.findAllChatMessages(pageable).map(c -> new ChatListResponse(c.getMessage(), c.getSentDate(), c.getUser().getNickname(), c.getUser().getImageUrl()));

    }

}
