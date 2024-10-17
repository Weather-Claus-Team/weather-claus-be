package com.weatherclaus.be.weather.exception;

public class InvalidLatLonException extends RuntimeException {
    public InvalidLatLonException(String message) {
        super(message);
    }
}
