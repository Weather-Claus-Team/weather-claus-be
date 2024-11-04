package com.weatherclaus.be.user.service;

import com.weatherclaus.be.user.dto.request.*;
import com.weatherclaus.be.user.dto.response.UserInfoResponse;
import com.weatherclaus.be.user.entity.Role;
import com.weatherclaus.be.user.entity.User;
import com.weatherclaus.be.user.exception.*;
import com.weatherclaus.be.user.repository.UserRepsotiroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepsotiroy userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RecaptchaService recaptchaService;
    private final EmailService emailService;

    @Value("${USER_BASIC_IMAGE}")
    private String imageUrl;


    public void registerUser(JoinRequest joinRequest) {


//        recaptchaCheck(joinDTO.getToken());
        checkEmailDuplicate(joinRequest.getEmail());
        usernameDuplicateCheck(joinRequest.getUsername());

        User user = createUser(joinRequest);
        saveUser(user);
    }



    private void recaptchaCheck(String token) {

        boolean isHuman = recaptchaService.verifyRecaptcha(token);

        if (!isHuman) {
            throw new RecaptchaTokenInvalidException("invalid recaptcha token");
        }
    }


    // 이메일 중복 확인 메서드
    private void checkEmailDuplicate(String email) {
        Boolean isExistEmail = userRepository.existsByEmail(email);
        if (isExistEmail) {
            throw new EmailAlreadyExistsException("Email is already in use.");
        }
    }

    // 사용자명 중복 확인 메서드
    public void usernameDuplicateCheck(String username) {
        Boolean isExistUsername = userRepository.existsByUsername(username);
        if (isExistUsername) {
            throw new UserAlreadyExistsException("Username is already in use.");
        }
    }

    // User 엔터티 생성 메서드
    private User createUser(JoinRequest joinRequest) {
        return User.builder()
                .username(joinRequest.getUsername())
                .email(joinRequest.getEmail())
                .nickname(UUID.randomUUID().toString())
                .role(Role.USER) // 기본적으로 USER 설정, 필요에 따라 변경 가능
                .password(bCryptPasswordEncoder.encode(joinRequest.getPassword()))
                .imageUrl(imageUrl)
                .build();
    }

    // User 저장 메서드
    private void saveUser(User user) {
        userRepository.save(user);
    }


    public void sendEmailVerification(String email) {

        if(userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("email already exists");
        }

        emailService.sendContactEmail(email);
    }


    public void verifyEmailCode(EmailCodeRequest emailCodeRequest) {
        emailService.verifyCode(emailCodeRequest);
    }


    public User findByUsername(String username) {

        User user = userRepository.findByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("Username not found.");
        }
        return user;
    }


    public void sendUsername(EmailRequest emailRequest) {

        User user = userRepository.findByEmail(emailRequest.getEmail());

        if(user == null) {
            throw new EmailNotFoundException("Email not found.");
        }

        emailService.sendUsername(user.getEmail(), user.getUsername());

    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

}