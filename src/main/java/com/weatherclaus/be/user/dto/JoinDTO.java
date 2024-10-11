package com.weatherclaus.be.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDTO {

    private String username;
    private String email;
    private String password;
    private String checkpassword;
}
