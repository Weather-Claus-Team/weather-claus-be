package com.weatherclaus.be.user.service;


import com.weatherclaus.be.user.dto.response.RecaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    @Value("${RECAPTCHA_SECRET_KEY}")
    private String secretKey;

    public boolean verifyRecaptcha(String token) {

        String url = "https://www.google.com/recaptcha/api/siteverify";
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);

        // 헤더와 바디를 포함한 HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // POST 요청 보내기
        RecaptchaResponse response = restTemplate.postForObject(url, requestEntity, RecaptchaResponse.class);
        return response != null && response.isSuccess();
    }
}
