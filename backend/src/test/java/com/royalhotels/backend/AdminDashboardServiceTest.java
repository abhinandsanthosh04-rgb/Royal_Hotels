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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AdminDashboardServiceTest - Unit tests for admin statistics, search, and status updates.
 */
@ExtendWith(MockitoExtension.class)
public class AdminDashboardServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    private Room mockRoom;
    private Customer mockCustomer;
    private Booking mockBooking;
    private Payment mockPayment;

    @BeforeEach
    void setUp() {
        mockRoom = new Room();
        mockRoom.setRoomId(101);
        mockRoom.setRoomNumber("101");
        mockRoom.setRoomType("Standard");
        mockRoom.setPricePerNight(BigDecimal.valueOf(3500.00));
        mockRoom.setStatus("AVAILABLE");

        mockCustomer = new Customer();
        mockCustomer.setCustomerId(1);
        mockCustomer.setName("Vikramaditya Rathore");
        mockCustomer.setEmail("vikram@example.com");
        mockCustomer.setPhone("9876543210");
        mockCustomer.setCreatedAt(LocalDateTime.now());

        mockBooking = new Booking();
        mockBooking.setBookingId(501);
        mockBooking.setRoom(mockRoom);
        mockBooking.setCustomer(mockCustomer);
        mockBooking.setCheckIn(LocalDate.of(2026, 11, 1));
        mockBooking.setCheckOut(LocalDate.of(2026, 11, 3));
        mockBooking.setNumberOfGuests(2);
        mockBooking.setNumberOfNights(2);
        mockBooking.setTotalAmount(BigDecimal.valueOf(7000.00));
        mockBooking.setBookingStatus("CONFIRMED");
        mockBooking.setCreatedAt(LocalDateTime.now());

        mockPayment = new Payment();
        mockPayment.setPaymentId(901);
        mockPayment.setBooking(mockBooking);
        mockPayment.setAmount(BigDecimal.valueOf(7000.00));
        mockPayment.setPaymentMethod("UPI");
        mockPayment.setPaymentStatus("PAID");
        mockPayment.setPaymentDate(LocalDate.now());
    }

    @Test
    @DisplayName("getStats calculates total revenue and counts correctly")
    void testGetStats() {
        when(bookingRepository.findAll()).thenReturn(List.of(mockBooking));
        when(customerRepository.findAll()).thenReturn(List.of(mockCustomer));
        when(roomRepository.findAll()).thenReturn(List.of(mockRoom));
        when(paymentRepository.findAll()).thenReturn(List.of(mockPayment));

        AdminStatsResponse stats = adminDashboardService.getStats();

        assertNotNull(stats);
        assertEquals(1, stats.getTotalBookings());
        assertEquals(1, stats.getConfirmedBookings());
        assertEquals(1, stats.getTotalCustomers());
        assertEquals(1, stats.getTotalRooms());
        assertEquals(BigDecimal.valueOf(7000.00), stats.getTotalRevenue());
    }

    @Test
    @DisplayName("updateBookingStatus transitions status and updates room occupancy")
    void testUpdateBookingStatus() {
        when(bookingRepository.findById(501)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);

        AdminBookingItem result = adminDashboardService.updateBookingStatus(501, "CHECKED_IN");

        assertNotNull(result);
        assertEquals("CHECKED_IN", mockBooking.getBookingStatus());
        assertEquals("OCCUPIED", mockRoom.getStatus());
        verify(bookingRepository).save(mockBooking);
        verify(roomRepository).save(mockRoom);
    }

    @Test
    @DisplayName("JWT Token Service generates and validates cryptographic tokens")
    void testJwtTokenService() {
        JwtTokenService jwtService = new JwtTokenService(
                "SuperSecureTestSecretKeyForAdminAuthJwtTokens2026!", 3600000L);

        String token = jwtService.generateToken("admin", "Admin Test", "ROLE_ADMIN");
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals("admin", jwtService.extractUsername(token));
        assertEquals("ROLE_ADMIN", jwtService.extractRole(token));
        assertEquals("Admin Test", jwtService.extractFullName(token));

        assertFalse(jwtService.validateToken("invalid.token.here"));
    }
}
