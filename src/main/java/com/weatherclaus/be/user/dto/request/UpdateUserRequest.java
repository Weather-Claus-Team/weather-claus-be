package com.weatherclaus.be.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserRequest {

    MultipartFile file;

    @NotBlank(message = "닉네임을 입력해주세요")
    String nickname;
}
