package com.weatherclaus.be.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinRequest {

    @NotBlank(message = "공백이면 안됩니다.")
    @Size(min = 4, max = 20 , message = "username은 4~20이어야합니다")
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Size(min = 6)
    private String password2;

//    private String token;
}
