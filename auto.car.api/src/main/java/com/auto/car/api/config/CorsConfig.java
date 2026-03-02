package com.auto.car.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final ApplicationConfig applicationConfig;

    @Autowired
    public CorsConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] OriginsUrl = applicationConfig.getAllowedOriginsUrl().split(";");
        registry.addMapping("/**")
                .allowedOrigins(OriginsUrl)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("FileResponse", "Authorization")
                .allowCredentials(true);
    }

}
