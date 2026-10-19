package com.royalhotels.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * RoomController - REST API Controller for room discovery and availability checks.
 *
 * OOP Concept: Controller Layer in MVC architecture. Handles HTTP requests,
 * delegates business calculations to RoomService, and formats HTTP responses.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * GET /api/rooms
     * Returns ALL rooms from the database.
     */
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    /**
     * GET /api/rooms/available
     *
     * Dual-purpose endpoint:
     * 1. Without query params: Returns all rooms with static status = 'AVAILABLE'.
     * 2. With query params (?checkIn=2026-10-10&checkOut=2026-10-12&guests=2):
     *    Checks dynamic date availability, validates dates, eliminates overlaps,
     *    and returns available rooms with backend-calculated nights and total price.
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableRooms(
            @RequestParam(name = "checkIn", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(name = "checkOut", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(name = "guests", required = false) Integer guests) {

        // If no date query parameters are provided, return static available rooms (backward compatible)
        if (checkIn == null && checkOut == null && guests == null) {
            return ResponseEntity.ok(roomService.getAvailableRooms());
        }

        try {
            int guestCount = (guests != null) ? guests : 1;
            List<AvailableRoomResponse> availableRooms = roomService.checkAvailability(checkIn, checkOut, guestCount);
            return ResponseEntity.ok(availableRooms);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /api/rooms/check-availability
     * Explicit GET endpoint for checking availability by date range and guest count.
     * Example: GET /api/rooms/check-availability?checkIn=2026-10-10&checkOut=2026-10-13&guests=2
     */
    @GetMapping("/check-availability")
    public ResponseEntity<?> checkAvailabilityGet(
            @RequestParam(name = "checkIn")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(name = "checkOut")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(name = "guests", defaultValue = "1") Integer guests) {
        try {
            List<AvailableRoomResponse> availableRooms = roomService.checkAvailability(checkIn, checkOut, guests);
            return ResponseEntity.ok(availableRooms);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * POST /api/rooms/check-availability
     * JSON payload endpoint for checking availability.
     * Example body: { "checkIn": "2026-10-10", "checkOut": "2026-10-13", "guests": 2 }
     */
    @PostMapping("/check-availability")
    public ResponseEntity<?> checkAvailabilityPost(@RequestBody AvailabilityRequest request) {
        try {
            if (request == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Request body is required."));
            }
            List<AvailableRoomResponse> availableRooms = roomService.checkAvailability(
                    request.getCheckIn(), request.getCheckOut(), request.getEffectiveGuests());
            return ResponseEntity.ok(availableRooms);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /api/rooms/{id}
     * Returns a single room by its primary key ID.
     * Declared AFTER specific endpoints so "{id}" does not intercept them.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Integer id) {
        Optional<Room> room = roomService.getRoomById(id);
        if (room.isPresent()) {
            return ResponseEntity.ok(room.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
