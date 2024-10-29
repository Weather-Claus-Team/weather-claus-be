package com.weatherclaus.be.user.service;

import com.weatherclaus.be.user.dto.request.CurrentPasswordRequest;
import com.weatherclaus.be.user.dto.request.UpdatePasswordRequest;
import com.weatherclaus.be.user.dto.request.UpdateUserRequest;
import com.weatherclaus.be.user.dto.response.UserInfoResponse;
import com.weatherclaus.be.user.entity.User;
import com.weatherclaus.be.user.exception.AuthenticationNotValid;
import com.weatherclaus.be.user.exception.PasswordMismatchException;
import com.weatherclaus.be.user.repository.UserRepsotiroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Service s3Service;
    private final UserRepsotiroy userRepository;

    @Value("${USER_BASIC_IMAGE}")
    private String basic_Image;



    private User getUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        // 인증되지 않은 경우 예외 처리 ( 방어 로직 )
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationNotValid("not valid Authentication");
        }

        String username = authentication.getName(); // 인증된 유저의 username 가져오기

        return userRepository.findByUsername(username);

    }

    // User 정보 메서드
    public UserInfoResponse getUserInfo() {


        User user = getUser();

        return new UserInfoResponse().builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .nickname(user.getNickname())
                .build();

    }

    public void updateUserInfo(UpdateUserRequest updateUserDTO) throws IOException {

        User user = getUser();

        String imageUrl = null;

        MultipartFile file = updateUserDTO.getFile();


        if (file == null || file.isEmpty()) {
//            기존 이미지를 그대로 사용한다.

        }else{
//            새로운 이미지가 업로드 된 경우

//            기본 이미지 일 경우 삭제하지 않는다
            if(user.getImageUrl().equals(basic_Image)){

            }else{
//            기본 이미지가 아닐 경우 삭제한다.
                s3Service.deleteFile(user.getImageUrl());

            }
//            업로드하고 변경감지를 이용한다.
            imageUrl = s3Service.uploadFile(file);
            user.changeImageUrl(imageUrl);

        }



        String nickname = updateUserDTO.getNickname();

        user.changeNickname(nickname);

    }

    public void isCurrentPassword(CurrentPasswordRequest currentPasswordDTO) {

        User user = getUser();

        if (!bCryptPasswordEncoder.matches (currentPasswordDTO.getPassword(), user.getPassword())) {

            throw new PasswordMismatchException("Passwords do not match.");
        }



    }

    public void updatePassword(UpdatePasswordRequest updatePasswordDTO) {


        User user = getUser();


        user.changePassword(bCryptPasswordEncoder.encode(updatePasswordDTO.getPassword()));

    }

    public void deleteUser() {
        User user = getUser();

        userRepository.delete(user);
    }

}
