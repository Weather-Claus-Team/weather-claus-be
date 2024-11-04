package com.weatherclaus.be.user.exception;

import com.weatherclaus.be.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)  // 높은 우선순위
@Slf4j
public class UserExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<?>> handleValidationExceptions(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();


        // 첫 번째 검증 오류 메시지만 가져오기 (필드명: 커스텀 메시지)
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(error -> error.getDefaultMessage()) // 커스텀 메시지만 추출
                .findFirst()
                .orElse("Validation error");

        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", errorMessage);

        log.error("MethodArgumentNotValidException: {}", errorMessage, e);




        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler({RecaptchaTokenInvalidException.class, UsernameNotFoundException.class, AuthenticationNotValid.class,EmailNotFoundException.class, CodeMismatchException.class,EmailAlreadyExistsException.class,UserAlreadyExistsException.class,PasswordMismatchException.class})
    public ResponseEntity<ResponseDto<?>> RecaptchaTokenInvalidCheck(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);

        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }
}
