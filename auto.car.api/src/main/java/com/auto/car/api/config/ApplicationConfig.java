package com.auto.car.api.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ApplicationConfig {
    @Value("${cors.allowedOrigins.url}")
    private String allowedOriginsUrl;

    @Value("${email.from}")
    private String emailFrom;
}
