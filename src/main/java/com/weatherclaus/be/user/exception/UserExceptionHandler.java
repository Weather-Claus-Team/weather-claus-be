package com.weatherclaus.be.user.exception;

import com.weatherclaus.be.common.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

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

    @ExceptionHandler(CodeMismatchException.class)
    public ResponseEntity<ResponseDto<?>> CodeMismatchException(CodeMismatchException e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecaptchaTokenInvalidException.class)
    public ResponseEntity<ResponseDto<?>> RecaptchaTokenInvalidCheck(RecaptchaTokenInvalidException e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }
}
