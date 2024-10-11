package com.weatherclaus.be.weather.exception;

import com.weatherclaus.be.user.exception.EmailAlreadyExistsException;
import com.weatherclaus.be.user.exception.PasswordMismatchException;
import com.weatherclaus.be.user.exception.UserAlreadyExistsException;
import com.weatherclaus.be.weather.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ResponseDto<?>> handlePasswordMismatchException(PasswordMismatchException e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);        }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseDto<?>> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseDto<?>> EmailAlreadyExistsException(EmailAlreadyExistsException e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleException(Exception e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);

    }

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

}
