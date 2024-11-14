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

@Service
@Slf4j
public class RecaptchaService {

    @Value("${RECAPTCHA_SECRET_KEY}")
    private String secretKey;

    public void verifyRecaptcha(String token) {

        String url = "https://www.google.com/recaptcha/api/siteverify";
        RestTemplate restTemplate = new RestTemplate();

        // 요청 파라미터를 form data 형식으로 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // 요청 전송 및 응답 수신
        RecaptchaResponse response = restTemplate.postForObject(url, requestEntity, RecaptchaResponse.class);

        log.info("Recaptcha response: {}", response);


        boolean result = response != null && response.isSuccess();
        if (!result) {
            log.error("Invalid recaptcha token");
            throw new RecaptchaTokenInvalidException("Invalid recaptcha token");
        }
    }
}

