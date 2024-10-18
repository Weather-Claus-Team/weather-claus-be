package com.weatherclaus.be.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameDTO {

    @NotBlank
    @Size(min = 4, max = 20)
    private String username;
}
