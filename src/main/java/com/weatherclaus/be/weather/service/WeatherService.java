package com.weatherclaus.be.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherclaus.be.weather.dto.LatLonDTO;
import com.weatherclaus.be.weather.dto.WeatherResponse;
import com.weatherclaus.be.weather.exception.CityNotFoundException;
import com.weatherclaus.be.weather.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@RequiredArgsConstructor
public class WeatherService {

    private final ObjectMapper objectMapper;

    @Value("${DEFAULT_LAT}")
    private double DEFAULT_LAT;

    @Value("${DEFAULT_LON}")
    private double DEFAULT_LON;

    @Value("${WEATHER_API_URL}")
    private String WEATHER_API_URL;

    @Value("${GEOCODING_API_URL}")
    private String GEOCODING_API_URL;

    @Value("${OPENWEATHERMAP_API_KEY}")
    private String apiKey;

    public LatLonDTO resolveLatLon(String city, Double lat, Double lon) throws JsonProcessingException {
        if (city != null) {
            return getLatLon(city);
        } else if (lat == 0 && lon == 0) {
            return new LatLonDTO(DEFAULT_LAT, DEFAULT_LON);
        }
        return new LatLonDTO(lat, lon);
    }

    @Cacheable(value = "cityCache", key = "#city")
    public LatLonDTO getLatLon(String city) throws JsonProcessingException {

        String url = String.format(GEOCODING_API_URL, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();

        String jsonData = null;

        try {

            jsonData = restTemplate.getForObject(url, String.class);

        } catch (RestClientException e) {
            throw new ExternalApiException("Failed to retrieve data from weather/geocoding API");
        }

        List<LatLonDTO> weatherDTOList = objectMapper.readValue(jsonData, new TypeReference<>() {});

        if (weatherDTOList == null || weatherDTOList.isEmpty()) {
            throw new CityNotFoundException(city + " not found");
        }

        return weatherDTOList.get(0);
    }

    // 3km 범위로 캐시 키 생성 메서드
    public String createCacheKey(double lat, double lon) {
        double gridLat = Math.floor(lat / 0.03);
        double gridLon = Math.floor(lon / 0.03);
        return gridLat + "," + gridLon;
    }

    @Cacheable(value = "weatherCache", key = "#cacheKey")
    public WeatherResponse getWeather(String cacheKey, double lat, double lon) throws JsonProcessingException {
        // Weather API 호출
        String url = String.format(WEATHER_API_URL, lat, lon, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String jsonData = null;

        try {

            jsonData = restTemplate.getForObject(url, String.class);

        } catch (RestClientException e) {
            throw new ExternalApiException("Failed to retrieve data from weather/geocoding API");
        }


        return objectMapper.readValue(jsonData, WeatherResponse.class);
    }


}
