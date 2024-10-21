package com.weatherclaus.be.user.exception;

public class AuthenticationNotValid extends RuntimeException {
  public AuthenticationNotValid(String message) {
    super(message);
  }
}
