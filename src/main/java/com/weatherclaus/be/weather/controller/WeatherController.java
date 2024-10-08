package com.weatherclaus.be.weather.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weatherclaus.be.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public String getWeather(
            @RequestParam(required = false, defaultValue = "0") Double lat,
            @RequestParam(required = false, defaultValue = "0") Double lon,
            @RequestParam(required = false) String city) throws JsonProcessingException {

//        위 경도로 넘어왔을 때
        if(lat != 0 && lon != 0) {

            // 캐시 키 생성
            String cacheKey = weatherService.createCacheKey(lat, lon);

            // WeatherService를 통해 캐시 처리
            return weatherService.getWeather(cacheKey, lat, lon);

//         도시 이름이 넘어왔을 때
        }else if(city != null) {


            String[] split = weatherService.getLatLon(city).split(",");

            lat = Double.parseDouble(split[0]);
            lon = Double.parseDouble(split[1]);



            String cacheKey = weatherService.createCacheKey(lat, lon);

            return weatherService.getWeather(cacheKey, lat, lon);

//            아무것도 입력받지 않은 기본값.
        }else{

            return weatherService.getWeather("hihi", 37.2633325, 127.0287472);
        }
    }
}