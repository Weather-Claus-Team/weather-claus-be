package com.weatherclaus.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // JPA Auditing 활성화
public class WeatherClausBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherClausBeApplication.class, args);
    }

}
