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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * BookingServiceTest - Unit tests for booking creation, backend price calculation,
 * and date overlap enforcement.
 */
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BookingService bookingService;

    private Room deluxeRoom;
    private Customer customer;

    @BeforeEach
    void setUp() {
        deluxeRoom = new Room();
        deluxeRoom.setRoomId(4);
        deluxeRoom.setRoomNumber("201");
        deluxeRoom.setRoomType("Deluxe");
        deluxeRoom.setPricePerNight(new BigDecimal("6500.00"));
        deluxeRoom.setCapacity(2);
        deluxeRoom.setStatus("AVAILABLE");
        deluxeRoom.setCreatedAt(LocalDateTime.now());

        customer = new Customer();
        customer.setCustomerId(10);
        customer.setName("Vikram Singh");
        customer.setEmail("vikram@example.com");
        customer.setPhone("9876543210");
        customer.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully create booking and calculate total amount on backend")
    void testCreateBookingSuccess() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(4);
        request.setCustomerName("Vikram Singh");
        request.setCustomerEmail("vikram@example.com");
        request.setCustomerPhone("9876543210");
        request.setCheckIn(LocalDate.now().plusDays(5));
        request.setCheckOut(LocalDate.now().plusDays(7)); // 2 nights
        request.setNumberOfGuests(2);

        when(roomRepository.findById(4)).thenReturn(Optional.of(deluxeRoom));
        when(bookingRepository.findOverlappingBookings(4, request.getCheckIn(), request.getCheckOut()))
                .thenReturn(Collections.emptyList());
        when(customerRepository.findByEmail("vikram@example.com")).thenReturn(Optional.of(customer));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setBookingId(101);
            return b;
        });

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(101, response.getBookingId());
        assertEquals(2, response.getNumberOfNights());
        assertEquals(new BigDecimal("6500.00"), response.getPricePerNight());
        // 6500 * 2 = 13000
        assertEquals(new BigDecimal("13000.00"), response.getTotalAmount());
        assertEquals("CONFIRMED", response.getBookingStatus());
    }

    @Test
    @DisplayName("Should reject booking when room has overlapping CONFIRMED/CHECKED_IN booking")
    void testOverlappingBookingConflict() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(4);
        request.setCustomerName("Vikram Singh");
        request.setCustomerEmail("vikram@example.com");
        request.setCustomerPhone("9876543210");
        request.setCheckIn(LocalDate.now().plusDays(5));
        request.setCheckOut(LocalDate.now().plusDays(7));
        request.setNumberOfGuests(2);

        Booking existingBooking = new Booking();
        existingBooking.setBookingId(99);
        existingBooking.setBookingStatus("CONFIRMED");

        when(roomRepository.findById(4)).thenReturn(Optional.of(deluxeRoom));
        when(bookingRepository.findOverlappingBookings(4, request.getCheckIn(), request.getCheckOut()))
                .thenReturn(List.of(existingBooking));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                bookingService.createBooking(request));

        assertTrue(ex.getMessage().contains("already reserved"));
    }

    @Test
    @DisplayName("Should reject booking if room capacity is exceeded")
    void testExceededCapacityRejected() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(4);
        request.setCustomerName("Vikram Singh");
        request.setCustomerEmail("vikram@example.com");
        request.setCustomerPhone("9876543210");
        request.setCheckIn(LocalDate.now().plusDays(5));
        request.setCheckOut(LocalDate.now().plusDays(7));
        request.setNumberOfGuests(5); // Room capacity is 2

        when(roomRepository.findById(4)).thenReturn(Optional.of(deluxeRoom));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                bookingService.createBooking(request));

        assertTrue(ex.getMessage().contains("capacity"));
    }
}
