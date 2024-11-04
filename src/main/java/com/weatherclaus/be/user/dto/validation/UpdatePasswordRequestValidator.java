package com.weatherclaus.be.user.dto.validation;

import com.weatherclaus.be.user.dto.request.JoinRequest;
import com.weatherclaus.be.user.dto.request.UpdatePasswordRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UpdatePasswordRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdatePasswordRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UpdatePasswordRequest request = (UpdatePasswordRequest) target;

        // 두 개의 비밀번호가 일치하는지 검증
        if (!request.getPassword().equals(request.getPassword2())) {
//            throw new PasswordMismatchException("Passwords do not match");
            errors.rejectValue("password", "PasswordMismatch", "Passwords do not match");

        }

    }

}
