package com.weatherclaus.be.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    private String cod;
    private int message;
    private int cnt;
    private List<WeatherData> list;
    private City city;

}
