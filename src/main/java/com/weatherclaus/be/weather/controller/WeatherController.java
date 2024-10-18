package com.weatherclaus.be.weather.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weatherclaus.be.common.ResponseDto;
import com.weatherclaus.be.weather.dto.LatLonDTO;
import com.weatherclaus.be.weather.dto.WeatherResponse;
import com.weatherclaus.be.weather.exception.InvalidLatLonException;
import com.weatherclaus.be.weather.exception.WeatherResponseNullException;
import com.weatherclaus.be.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/api/weather"))
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;


    @GetMapping("/forecast")
    public ResponseEntity<ResponseDto<?>> getWeather(
            @RequestParam(required = false, defaultValue = "0") Double lat,
            @RequestParam(required = false, defaultValue = "0") Double lon,
            @RequestParam(required = false) String city) throws JsonProcessingException {


        log.info("Weather request received for lat: {}, lon: {}, city: {}", lat, lon, city);


        // 1. 위도/경도 유효성 검증
        if ((lat != 0 && (lat < -90 || lat > 90)) || (lon != 0 && (lon < -180 || lon > 180))) {
            throw new InvalidLatLonException("Invalid latitude or longitude range");
        }


        LatLonDTO latLon = weatherService.resolveLatLon(city, lat, lon);

        lat = latLon.getLat();
        lon = latLon.getLon();


        String cacheKey = weatherService.createCacheKey(lat, lon);

        WeatherResponse weatherResponse = weatherService.getWeather(cacheKey, lat, lon);

        if(weatherResponse == null) {
            throw new WeatherResponseNullException("WeatherResponse is null");
        }

        return new ResponseEntity<>(
                new ResponseDto<>("success", "Data retrieved successfully", weatherResponse, null, 200),
                HttpStatus.OK
        );
    }
}