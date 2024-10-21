package com.weatherclaus.be.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserRequest {


    MultipartFile file;
//    String nickname;
}
