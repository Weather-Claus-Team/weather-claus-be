package com.weatherclaus.be.user.service;

import com.weatherclaus.be.common.ResponseDto;
import com.weatherclaus.be.user.dto.JoinDTO;
import com.weatherclaus.be.user.entity.Role;
import com.weatherclaus.be.user.entity.User;
import com.weatherclaus.be.user.exception.EmailAlreadyExistsException;
import com.weatherclaus.be.user.exception.PasswordMismatchException;
import com.weatherclaus.be.user.exception.RecaptchaTokenInvalidException;
import com.weatherclaus.be.user.exception.UserAlreadyExistsException;
import com.weatherclaus.be.user.repository.UserRepsotiroy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {

    private final UserRepsotiroy userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RecaptchaService recaptchaService;


    public void joinProcess(JoinDTO joinDTO) {


        recaptchaCheck(joinDTO.getToken());

        validatePasswords(joinDTO.getPassword(), joinDTO.getPassword2());
        checkEmailDuplicate(joinDTO.getEmail());
        usernameDuplicateCheck(joinDTO.getUsername());

        User user = createUser(joinDTO);
        saveUser(user);
    }

    private void recaptchaCheck(String token) {

        boolean isHuman = recaptchaService.verifyRecaptcha(token);

        if (!isHuman) {
            throw new RecaptchaTokenInvalidException("invalid recaptcha token");
        }
    }

    // 비밀번호 확인 메서드
    private void validatePasswords(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new PasswordMismatchException("Passwords do not match.");
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
    private User createUser(JoinDTO joinDTO) {
        return User.builder()
                .username(joinDTO.getUsername())
                .password(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
                .email(joinDTO.getEmail())
                .role(Role.USER) // 기본적으로 USER 설정, 필요에 따라 변경 가능
                .build();
    }

    // User 저장 메서드
    private void saveUser(User user) {
        userRepository.save(user);
    }
}