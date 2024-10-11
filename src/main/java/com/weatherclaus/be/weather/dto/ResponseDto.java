package com.weatherclaus.be.weather.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto<T> {
    private String status;   // "success" 또는 "fail"
    private String message;  // 성공 또는 실패 메시지
    private T data;          // 응답 데이터 (성공 시)
    private ErrorDetails error;  // 실패 시 오류 정보
    private int code;        // 상태 코드

    public ResponseDto(String status, String message, T data, ErrorDetails error, int code) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.error = error;
        this.code = code;
    }

    // ErrorDetails 클래스 정의
    @Getter
    @Setter
    public static class ErrorDetails {
        private String message;
        private String details;

        public ErrorDetails(String message, String details) {
            this.message = message;
            this.details = details;
        }

    }
}
