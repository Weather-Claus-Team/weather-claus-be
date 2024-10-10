package com.weatherclaus.be.weather.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class City {

    private Long id;
    private String name;
    private Coord coord;
    private String country;
    private Integer population;
    private String timezone;
    private String sunrise;
    private String sunset;

}
