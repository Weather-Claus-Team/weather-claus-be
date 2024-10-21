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



        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 410),
                HttpStatus.BAD_REQUEST);
    }

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

    @ExceptionHandler({RecaptchaTokenInvalidException.class, UsernameNotFoundException.class, AuthenticationNotValid.class})
    public ResponseEntity<ResponseDto<?>> RecaptchaTokenInvalidCheck(Exception e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        // 실패 시 400 상태 코드로 에러 응답
        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }
}
