package com.weatherclaus.be.user.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponse {


    String username;
    String email;
    String imageUrl;
    String nickname;


    @Builder
    public UserInfoResponse(String username, String email, String imageUrl,String nickname) {
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
    }
}
