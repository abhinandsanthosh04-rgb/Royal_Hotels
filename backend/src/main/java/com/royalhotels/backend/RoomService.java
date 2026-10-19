package com.royalhotels.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RoomService - Business Logic Layer for rooms and availability.
 *
 * OOP Concepts Demonstrated:
 * - Separation of Concerns: Business validation and calculations live in the Service layer,
 *   not in the Controller or Repository.
 * - Encapsulation: Room pricing and availability calculations are encapsulated within
 *   service methods.
 */
@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Get ALL rooms from the database (any status).
     *
     * @return a list of every Room record in the 'rooms' table
     */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Get ONE room by its ID from the database.
     *
     * @param id - the room_id to search for
     * @return an Optional containing the Room if found, or empty if not found
     */
    public Optional<Room> getRoomById(Integer id) {
        return roomRepository.findById(id);
    }

    /**
     * Get rooms with static status = 'AVAILABLE'.
     *
     * @return a list of rooms whose status column equals 'AVAILABLE'
     */
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus("AVAILABLE");
    }

    /**
     * Check dynamic room availability for requested dates and guest count.
     *
     * Business Rules:
     * 1. Validates that checkIn and checkOut are provided and checkIn < checkOut.
     * 2. Validates that checkIn is not in the past.
     * 3. Validates that guest count is positive (>= 1).
     * 4. Treats check-in as inclusive and check-out as exclusive.
     * 5. Filters out rooms under MAINTENANCE or with capacity < guests.
     * 6. Filters out rooms with overlapping CONFIRMED or CHECKED_IN bookings.
     * 7. Calculates number of nights and total price on the backend using the
     *    room's database price (price_per_night * numberOfNights).
     *
     * @param checkIn  requested check-in date (inclusive)
     * @param checkOut requested check-out date (exclusive)
     * @param guests   number of guests
     * @return list of AvailableRoomResponse DTOs with calculated prices
     * @throws IllegalArgumentException on invalid input dates or guest count
     */
    public List<AvailableRoomResponse> checkAvailability(LocalDate checkIn, LocalDate checkOut, Integer guests) {
        // Validation: Required fields
        if (checkIn == null) {
            throw new IllegalArgumentException("Check-in date is required (format: YYYY-MM-DD).");
        }
        if (checkOut == null) {
            throw new IllegalArgumentException("Check-out date is required (format: YYYY-MM-DD).");
        }

        // Validation: Guest count positive
        if (guests == null || guests <= 0) {
            throw new IllegalArgumentException("Number of guests must be at least 1.");
        }

        // Validation: Check-in before check-out
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be strictly after check-in date.");
        }

        // Validation: Check-in not in the past
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        // Calculate nights: Check-in inclusive, check-out exclusive (e.g. Oct 10 to Oct 12 = 2 nights)
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        int numberOfNights = (int) nights;

        // Query database for rooms that meet capacity, not in maintenance, and have no overlapping bookings
        List<Room> eligibleRooms = roomRepository.findAvailableRoomsForDates(checkIn, checkOut, guests);

        // Map Room entities to AvailableRoomResponse DTOs with backend-calculated total price
        List<AvailableRoomResponse> results = new ArrayList<>();
        for (Room room : eligibleRooms) {
            BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
            results.add(new AvailableRoomResponse(room, checkIn, checkOut, numberOfNights, totalPrice));
        }

        return results;
    }
}
