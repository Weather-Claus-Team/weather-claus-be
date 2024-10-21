package com.weatherclaus.be.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {


    private String password;
    private String password2;
}
