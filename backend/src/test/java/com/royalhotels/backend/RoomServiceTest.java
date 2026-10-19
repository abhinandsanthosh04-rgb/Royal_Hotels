package com.royalhotels.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * RoomServiceTest - Unit tests for room availability logic, date overlap rules,
 * input validation, and backend pricing calculations.
 *
 * OOP Concept: Unit Testing & Verification of Encapsulated Business Rules.
 */
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room standardRoom;

    @BeforeEach
    void setUp() {
        standardRoom = new Room();
        standardRoom.setRoomId(1);
        standardRoom.setRoomNumber("101");
        standardRoom.setRoomType("Standard");
        standardRoom.setPricePerNight(new BigDecimal("3500.00"));
        standardRoom.setCapacity(2);
        standardRoom.setStatus("AVAILABLE");
        standardRoom.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully calculate nights and total price from database price")
    void testAvailabilityCalculationSuccess() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(13); // 3 nights
        int guests = 2;

        when(roomRepository.findAvailableRoomsForDates(checkIn, checkOut, guests))
                .thenReturn(List.of(standardRoom));

        List<AvailableRoomResponse> results = roomService.checkAvailability(checkIn, checkOut, guests);

        assertNotNull(results);
        assertEquals(1, results.size());

        AvailableRoomResponse roomResp = results.get(0);
        assertEquals(1, roomResp.getRoomId());
        assertEquals("101", roomResp.getRoomNumber());
        assertEquals(3, roomResp.getNumberOfNights());
        assertEquals(new BigDecimal("3500.00"), roomResp.getPricePerNight());
        // 3500.00 * 3 nights = 10500.00
        assertEquals(new BigDecimal("10500.00"), roomResp.getTotalPrice());
    }

    @Test
    @DisplayName("Should reject check-in date in the past")
    void testPastCheckInDateRejected() {
        LocalDate checkIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                roomService.checkAvailability(checkIn, checkOut, 2));

        assertTrue(ex.getMessage().contains("past"));
    }

    @Test
    @DisplayName("Should reject check-out date that is not after check-in")
    void testCheckOutBeforeOrEqualCheckInRejected() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOutSameDay = checkIn;
        LocalDate checkOutBefore = checkIn.minusDays(1);

        assertThrows(IllegalArgumentException.class, () ->
                roomService.checkAvailability(checkIn, checkOutSameDay, 2));

        assertThrows(IllegalArgumentException.class, () ->
                roomService.checkAvailability(checkIn, checkOutBefore, 2));
    }

    @Test
    @DisplayName("Should reject zero or negative guest count")
    void testZeroOrNegativeGuestCountRejected() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(7);

        assertThrows(IllegalArgumentException.class, () ->
                roomService.checkAvailability(checkIn, checkOut, 0));

        assertThrows(IllegalArgumentException.class, () ->
                roomService.checkAvailability(checkIn, checkOut, -2));
    }

    @Test
    @DisplayName("Should reject null dates")
    void testNullDatesRejected() {
        LocalDate validDate = LocalDate.now().plusDays(5);

        assertThrows(IllegalArgumentException.class, () ->
                roomService.checkAvailability(null, validDate, 1));

        assertThrows(IllegalArgumentException.class, () ->
                roomService.checkAvailability(validDate, null, 1));
    }

    @Test
    @DisplayName("Should return empty list when no room is available for dates/capacity")
    void testNoRoomsAvailable() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(7);

        when(roomRepository.findAvailableRoomsForDates(any(), any(), eq(4)))
                .thenReturn(Collections.emptyList());

        List<AvailableRoomResponse> results = roomService.checkAvailability(checkIn, checkOut, 4);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
