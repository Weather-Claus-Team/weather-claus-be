package com.weatherclaus.be.user.service;

import com.weatherclaus.be.user.dto.JoinDTO;
import com.weatherclaus.be.user.entity.Role;
import com.weatherclaus.be.user.entity.User;
import com.weatherclaus.be.user.exception.EmailAlreadyExistsException;
import com.weatherclaus.be.user.exception.PasswordMismatchException;
import com.weatherclaus.be.user.exception.UserAlreadyExistsException;
import com.weatherclaus.be.user.repository.UserRepsotiroy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {

    private final UserRepsotiroy userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String email = joinDTO.getEmail();
        String password = joinDTO.getPassword();
        String checkpassword = joinDTO.getCheckpassword();

        Boolean isExistUsername = userRepository.existsByUsername(username);

        Boolean isExistEmail = userRepository.existsByEmail(email);

        if (!password.equals(checkpassword)) {
            throw new PasswordMismatchException("Passwords do not match.");
        }

        if (isExistUsername) {
            throw new UserAlreadyExistsException("Username is already in use.");
        }

        if(isExistEmail){
            throw new EmailAlreadyExistsException("Email is already in use.");

        }

        User user = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .email(joinDTO.getEmail())
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);
    }
}