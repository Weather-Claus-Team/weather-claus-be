package com.weatherclaus.be.user.exception;

public class RecaptchaTokenInvalidException extends RuntimeException {
    public RecaptchaTokenInvalidException(String message) {
        super(message);
    }
}
