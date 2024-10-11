package com.weatherclaus.be.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    private long dt;
    private Main main;
    private List<Weather> weather;
    private int visibility;
    private double pop;
    private String dt_txt;
}