package com.weatherclaus.be.user.service;

import com.weatherclaus.be.user.dto.request.CurrentPasswordRequest;
import com.weatherclaus.be.user.dto.request.JoinRequest;
import com.weatherclaus.be.user.dto.request.UpdatePasswordRequest;
import com.weatherclaus.be.user.dto.request.UpdateUserRequest;
import com.weatherclaus.be.user.dto.response.UserInfoResponse;
import com.weatherclaus.be.user.entity.Role;
import com.weatherclaus.be.user.entity.User;
import com.weatherclaus.be.user.exception.*;
import com.weatherclaus.be.user.repository.UserRepsotiroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepsotiroy userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RecaptchaService recaptchaService;
    private final S3Service s3Service;


    public void registerUser(JoinRequest joinDTO) {


//        recaptchaCheck(joinDTO.getToken());

//        validatePasswords(joinDTO.getPassword(), joinDTO.getPassword2());
        checkEmailDuplicate(joinDTO.getEmail());
        usernameDuplicateCheck(joinDTO.getUsername());

        User user = createUser(joinDTO);
        saveUser(user);
    }

    private User getUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        // 인증되지 않은 경우 예외 처리 ( 방어 로직 )
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationNotValid("not valid Authentication");
        }

        String username = authentication.getName(); // 인증된 유저의 username 가져오기

        return userRepository.findByUsername(username);
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
    private User createUser(JoinRequest joinDTO) {
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

    // User 정보 메서드
    public UserInfoResponse getUserInfo() {


        User user = getUser();

        return new UserInfoResponse().builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .build();

    }

    public void updateUserInfo(UpdateUserRequest updateUserDTO) throws IOException {


        User user = getUser();


        if(user.getImageUrl()!=null){
            log.info(user.getImageUrl() + " log zzzz");
            s3Service.deleteFile(user.getImageUrl());
        }


        String imageUrl = s3Service.uploadFile(updateUserDTO.getFile());


        user.changeImageUrl(imageUrl);


    }

    public void isCurrentPassword(CurrentPasswordRequest currentPasswordDTO) {

        User user = getUser();

        if (!bCryptPasswordEncoder.matches (currentPasswordDTO.getPassword(), user.getPassword())) {

            throw new PasswordMismatchException("Passwords do not match.");
        }



    }

    public void updatePassword(UpdatePasswordRequest updatePasswordDTO) {


        if(!updatePasswordDTO.getPassword2().equals(updatePasswordDTO.getPassword())){
            throw new PasswordMismatchException("Passwords do not match.");
        }

        User user = getUser();


        user.changePassword(bCryptPasswordEncoder.encode(updatePasswordDTO.getPassword()));

    }

    public void deleteUser() {
        User user = getUser();

        userRepository.delete(user);
    }
}