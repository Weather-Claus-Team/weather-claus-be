package com.weatherclaus.be.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDTO {

    private double lat;
    private double lon;

}
