package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Frontend y backend comparten origen (localhost:8080), pero dejamos
                // CORS abierto por si el dev sirve el HTML desde otro puerto en algun momento.
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                "http://localhost:8080",
                                "http://localhost:5500",
                                "http://127.0.0.1:5500",
                                "http://localhost:3000",
                                "http://localhost:4200"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
