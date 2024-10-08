package com.weatherclaus.be.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherclaus.be.weather.dto.WeatherDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/forecast?lang=kr&units=metric&lat=%s&lon=%s&appid=%s";

    private static final String GEOCODING_API_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=5&appid=%s";

    @Value("${OPENWEATHERMAP_API_KEY}")
    private String apiKey = "your-api-key";

    // 3km 범위로 캐시 키 생성 메서드
    public String createCacheKey(double lat, double lon) {
        double gridLat = Math.floor(lat / 0.03);
        double gridLon = Math.floor(lon / 0.03);
        return gridLat + "," + gridLon;
    }

//    @Cacheable(value = "weatherCache", key = "#cacheKey")
    public String getWeather(String cacheKey, double lat, double lon) {
        // Weather API 호출
        String url = String.format(WEATHER_API_URL, lat, lon, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }


    @Cacheable(value = "cityCache", key = "#city")
    public String getLatLon(String city) throws JsonProcessingException {

        String url = String.format(GEOCODING_API_URL, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String jsonData = restTemplate.getForObject(url, String.class);





        // ObjectMapper 사용하여 JSON을 List<LocationDto>로 변환
        ObjectMapper mapper = new ObjectMapper();
        List<WeatherDTO> locations = mapper.readValue(jsonData, new TypeReference<List<WeatherDTO>>() {
        });

        if(locations == null || locations.isEmpty()) {
            throw new IllegalArgumentException("잘못된 인자 ");
        }

        WeatherDTO location = locations.get(0);  // 첫 번째 결과 사용
//        Double[] arr = new Double[2];
//        arr[0] = location.getLat();
//        arr[1] = location.getLon();

        return location.getLat() + "," + location.getLon();
    }


}
