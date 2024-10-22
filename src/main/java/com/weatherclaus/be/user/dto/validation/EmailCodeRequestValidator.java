package com.weatherclaus.be.user.dto.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.weatherclaus.be.user.dto.request.EmailCodeRequest;

@Component
public class EmailCodeRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EmailCodeRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EmailCodeRequest request = (EmailCodeRequest) target;


        // code가 숫자인지 확인
        if (request.getCode() != null && !request.getCode().matches("\\d+")) {
            errors.rejectValue("code", "code.invalid", "Code는 숫자만 포함할 수 있습니다.");
        }
    }
}
