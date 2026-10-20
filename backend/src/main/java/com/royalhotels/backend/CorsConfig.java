package com.royalhotels.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig - Cross-Origin Resource Sharing configuration.
 *
 * OOP Concept: This is a Configuration class (a special kind of object).
 * It tells Spring Boot: "Allow the Angular app at localhost:4200 to send
 * HTTP requests to this backend at localhost:8080."
 *
 * Without CORS, browsers block requests between different ports for security.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")       // Apply to all /api/ endpoints
                        .allowedOriginPatterns(
                                "http://localhost:4200",
                                "http://127.0.0.1:4200",
                                "http://localhost:*",
                                "https://*.vercel.app",
                                "https://vercel.app",
                                "https://*.onrender.com"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}