package com.weatherclaus.be.weather.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weatherclaus.be.weather.dto.ResponseDto;
import com.weatherclaus.be.weather.dto.LatLonDTO;
import com.weatherclaus.be.weather.dto.WeatherResponse;
import com.weatherclaus.be.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/api/weather"))
public class WeatherController {

    private final WeatherService weatherService;

    private static final double DEFAULT_LAT = 37.5806949;
    private static final double DEFAULT_LON = 126.9827989;


    @GetMapping("/forecast")
    public ResponseEntity<ResponseDto<?>> getWeather(
            @RequestParam(required = false, defaultValue = "0") Double lat,
            @RequestParam(required = false, defaultValue = "0") Double lon,
            @RequestParam(required = false) String city) throws JsonProcessingException {

        //도시이름 기반
        if(city != null) {

            LatLonDTO latLon = weatherService.getLatLon(city);

            lat = latLon.getLat();

            lon = latLon.getLon();

        //디폴트 값
        }else if(lat == 0 && lon == 0){

            lat = DEFAULT_LAT;

            lon = DEFAULT_LON;

        }


        String cacheKey = weatherService.createCacheKey(lat, lon);


        WeatherResponse weatherResponse = weatherService.getWeather(cacheKey, lat, lon);

        return new ResponseEntity<>(
                new ResponseDto<>("success", "Data retrieved successfully", weatherResponse, null, 200),
                HttpStatus.OK
        );
    }
}