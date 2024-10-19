package com.weatherclaus.be.cofig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000",
                        "http://weather-claus-static-files.s3-website.ap-northeast-2.amazonaws.com",
                        "https://d2gm6q97x0ibvz.cloudfront.net",
                        "https://mungwithme.com",
                        "http://mungwithme.com",
                        "mungwithme.com",
                        "d2gm6q97x0ibvz.cloudfront.net"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Set-Cookie");  // Set-Cookie 노출 허용

    }
}
