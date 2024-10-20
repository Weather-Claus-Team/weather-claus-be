package com.weatherclaus.be.weather.exception;


import com.weatherclaus.be.common.ResponseDto;
import com.weatherclaus.be.user.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WeatherExceptionHandler {


    @ExceptionHandler({InvalidLatLonException.class,WeatherResponseNullException.class, ExternalApiException.class, CityNotFoundException.class})
    public ResponseEntity<ResponseDto<?>> handleInvalidLatLonException(Exception e) {
        ResponseDto.ErrorDetails errorDetails = new ResponseDto.ErrorDetails("Bad Request", e.getMessage());

        return new ResponseEntity<>(
                new ResponseDto<>("fail", "Invalid request", null, errorDetails, 400),
                HttpStatus.BAD_REQUEST);
    }

}
