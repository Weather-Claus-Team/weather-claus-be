package com.weatherclaus.be.weather.exception;

public class WeatherResponseNullException extends RuntimeException {
    public WeatherResponseNullException(String message) {
        super(message);
    }
}
