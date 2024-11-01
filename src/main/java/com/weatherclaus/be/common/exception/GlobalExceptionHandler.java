package com.weatherclaus.be.common.exception;

import com.weatherclaus.be.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(2)  // 높은 우선순위
@Slf4j
public class GlobalExceptionHandler {




    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage(), e);

        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Invalid Argument", "유효하지 않은 요청입니다.");

        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid argument", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseDto<?>> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException: {}", e.getMessage(), e);

        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Null Value", "유효하지 않은 값입니다.");

        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Null pointer exception", null, errorDetails, 500),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", "잘못된 요청입니다.");

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);

    }

}
