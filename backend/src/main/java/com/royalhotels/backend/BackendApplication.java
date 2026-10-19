package com.royalhotels.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BackendApplication - Entry point for the Royal Hotels Spring Boot
 * application.
 *
 * OOP Concept: This class is the starting point (main class) of the program.
 * 
 * @SpringBootApplication tells Spring to scan all classes in this package
 *                        and set up the web server automatically.
 */
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("======================================");
        System.out.println("  Royal Hotels Backend is running!");
        System.out.println("  Health check: http://localhost:8080/api/health");
        System.out.println("======================================");
    }
}