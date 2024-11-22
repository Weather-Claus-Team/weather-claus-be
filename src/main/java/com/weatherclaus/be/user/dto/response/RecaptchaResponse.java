package com.weatherclaus.be.user.dto.response;

import lombok.Data;

@Data
public class RecaptchaResponse {
    private boolean success = false; // 기본값을 false로 설정
}
