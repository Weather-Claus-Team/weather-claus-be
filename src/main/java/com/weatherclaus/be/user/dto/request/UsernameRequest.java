package com.weatherclaus.be.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    private String username;
}
