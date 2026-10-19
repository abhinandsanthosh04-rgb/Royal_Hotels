package com.royalhotels.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * HealthController - Handles the health-check REST API endpoint.
 *
 * OOP Concepts demonstrated:
 *  1. CLASS       : HealthController is a class (a blueprint/template).
 *  2. OBJECT      : Spring Boot creates one object of this class at startup.
 *  3. METHOD      : checkHealth() is a method (an action the object can do).
 *  4. ENCAPSULATION: The logic is neatly encapsulated inside this class.
 *  5. ABSTRACTION : The caller just calls GET /api/health; they do not need
 *                   to know how the response is built.
 *
 * @RestController tells Spring: "This class handles HTTP requests and
 *                 returns data (JSON) directly, not HTML pages."
 * @RequestMapping("/api") means all methods here start with /api/
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * GET /api/health
     *
     * Returns a JSON response confirming the backend is running.
     * Angular can call this endpoint to check if the backend is available.
     *
     * Example response:
     * {
     *   "status"      : "UP",
     *   "message"     : "Royal Hotels backend is running.",
     *   "timestamp"   : "2026-10-03T00:30:00"
     * }
     */
    @GetMapping("/health")
    public Map<String, String> checkHealth() {

        // Map is like a dictionary: key -> value
        Map<String, String> response = new HashMap<>();

        response.put("status",    "UP");
        response.put("message",   "Royal Hotels backend is running.");
        response.put("project",   "ROYAL HOTELS - Where Comfort Meets Luxury");
        response.put("timestamp", LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return response; // Spring automatically converts this Map to JSON
    }
}