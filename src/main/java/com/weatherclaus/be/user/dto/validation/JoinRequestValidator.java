package com.weatherclaus.be.user.dto.validation;

import com.weatherclaus.be.user.dto.request.JoinRequest;
import com.weatherclaus.be.user.exception.PasswordMismatchException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class JoinRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return JoinRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        JoinRequest joinRequest = (JoinRequest) target;


        // 비밀번호와 비밀번호 확인이 일치하는지 검증
        if (!joinRequest.getPassword().equals(joinRequest.getPassword2())) {
//            throw new PasswordMismatchException("Passwords do not match");
            errors.rejectValue("password", "PasswordMismatch", "Passwords do not match");

        }

    }
}
