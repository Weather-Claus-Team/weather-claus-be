package com.weatherclaus.be.common.exception;

import com.weatherclaus.be.common.ResponseDto;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(2)  // 높은 우선순위
public class GlobalExceptionHandler {




    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Invalid Argument", e.getMessage());

        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid argument", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseDto<?>> handleNullPointerException(NullPointerException e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Null Value", "A required value was null");

        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Null pointer exception", null, errorDetails, 500),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleException(Exception e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);

    }

}
