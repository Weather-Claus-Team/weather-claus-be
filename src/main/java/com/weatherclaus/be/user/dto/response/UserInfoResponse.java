package com.weatherclaus.be.user.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponse {


    String username;
    String email;
    String imageUrl;


    @Builder
    public UserInfoResponse(String username, String email, String imageUrl) {
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
    }
}
