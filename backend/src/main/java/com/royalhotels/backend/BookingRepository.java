package com.royalhotels.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * BookingRepository - Data Access Layer for the 'bookings' table.
 *
 * OOP Concept: Interface inheritance (extends JpaRepository).
 * Encapsulates SQL queries for booking validation and date overlap checks.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    /**
     * Find all bookings for a specific room.
     */
    List<Booking> findByRoom_RoomId(Integer roomId);

    /**
     * Find all bookings for a specific customer.
     */
    List<Booking> findByCustomer_CustomerId(Integer customerId);

    /**
     * Check for conflicting bookings for a specific room.
     *
     * Date Overlap Rule:
     *   Check-in is inclusive [checkIn,
     *   Check-out is exclusive , checkOut)
     *
     * Two intervals [b.checkIn, b.checkOut) and [requestedIn, requestedOut) overlap
     * if and only if:
     *   b.checkIn < requestedOut AND b.checkOut > requestedIn
     *
     * Only bookings with status 'CONFIRMED' or 'CHECKED_IN' block room availability.
     * 'CANCELLED' or 'CHECKED_OUT' bookings do not block availability.
     */
    @Query("SELECT b FROM Booking b WHERE b.room.roomId = :roomId " +
           "AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND b.checkIn < :checkOut AND b.checkOut > :checkIn")
    List<Booking> findOverlappingBookings(
        @Param("roomId") Integer roomId,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut
    );
}
