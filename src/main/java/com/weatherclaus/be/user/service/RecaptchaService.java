package com.weatherclaus.be.user.service;


import com.weatherclaus.be.user.dto.response.RecaptchaResponse;
import com.weatherclaus.be.user.exception.RecaptchaTokenInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RecaptchaService {

    @Value("${RECAPTCHA_SECRET_KEY}")
    private String secretKey;

    public void verifyRecaptcha(String token) {

        log.info("token"+token);

        String url = "https://www.google.com/recaptcha/api/siteverify";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> params = new HashMap<>();
        params.put("secret", secretKey);
        params.put("response", token);

        // 헤더와 바디를 포함한 HttpEntity 생성
        RecaptchaResponse response = restTemplate.postForObject(url, params, RecaptchaResponse.class);

        boolean result = response != null && response.isSuccess();

        if(!result) {
            log.info("invalid recaptcha token");
            throw new RecaptchaTokenInvalidException("invalid recaptcha token");
        }
    }
}
