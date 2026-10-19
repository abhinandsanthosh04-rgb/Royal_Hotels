package com.royalhotels.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * RoomRepository - Data Access Layer for the 'rooms' table.
 *
 * OOP Concept: Interface abstraction. Spring Data JPA generates the
 * implementation at runtime, abstracting SQL complexity.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    /**
     * Find all rooms that have a specific static status.
     * e.g. status = 'AVAILABLE'
     */
    List<Room> findByStatus(String status);

    /**
     * Find all rooms available for a dynamic date range and guest count.
     *
     * Rules enforced:
     * 1. Room capacity accommodates the guests: r.capacity >= :guests
     * 2. Room is not under maintenance: r.status <> 'MAINTENANCE'
     * 3. No conflicting booking exists with status 'CONFIRMED' or 'CHECKED_IN'.
     *    Date overlap logic (check-in inclusive, check-out exclusive):
     *    b.checkIn < :checkOut AND b.checkOut > :checkIn
     */
    @Query("SELECT r FROM Room r WHERE r.capacity >= :guests AND r.status <> 'MAINTENANCE' " +
           "AND NOT EXISTS (" +
           "    SELECT b FROM Booking b " +
           "    WHERE b.room = r " +
           "      AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "      AND b.checkIn < :checkOut " +
           "      AND b.checkOut > :checkIn" +
           ") ORDER BY r.roomId ASC")
    List<Room> findAvailableRoomsForDates(
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut,
        @Param("guests") Integer guests
    );
}
