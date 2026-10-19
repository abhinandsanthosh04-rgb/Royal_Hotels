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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PaymentServiceTest - Unit tests verifying payment processing, duplicate checks,
 * database amount authority, and simulated payment outcomes.
 */
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Booking sampleBooking;
    private Customer sampleCustomer;
    private Room sampleRoom;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(1);
        sampleCustomer.setName("Abhinand Santhosh");
        sampleCustomer.setEmail("abhinand@example.com");
        sampleCustomer.setPhone("9876543210");

        sampleRoom = new Room();
        sampleRoom.setRoomId(4);
        sampleRoom.setRoomNumber("201");
        sampleRoom.setRoomType("Deluxe");
        sampleRoom.setPricePerNight(new BigDecimal("6500.00"));
        sampleRoom.setCapacity(2);
        sampleRoom.setStatus("AVAILABLE");

        sampleBooking = new Booking();
        sampleBooking.setBookingId(10);
        sampleBooking.setCustomer(sampleCustomer);
        sampleBooking.setRoom(sampleRoom);
        sampleBooking.setCheckIn(LocalDate.now().plusDays(5));
        sampleBooking.setCheckOut(LocalDate.now().plusDays(8)); // 3 nights
        sampleBooking.setNumberOfGuests(2);
        sampleBooking.setNumberOfNights(3);
        sampleBooking.setTotalAmount(new BigDecimal("19500.00"));
        sampleBooking.setBookingStatus("CONFIRMED");
        sampleBooking.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully process payment using booking database amount")
    void testProcessPaymentSuccess() {
        PaymentRequest request = new PaymentRequest(10, "UPI", true);

        when(bookingRepository.findById(10)).thenReturn(Optional.of(sampleBooking));
        when(paymentRepository.existsByBooking_BookingIdAndPaymentStatus(10, "PAID")).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setPaymentId(501);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(501, response.getPaymentId());
        assertEquals(10, response.getBookingId());
        assertEquals("UPI", response.getPaymentMethod());
        assertEquals("PAID", response.getPaymentStatus());
        // Verify amount came from booking in DB (19500.00)
        assertEquals(new BigDecimal("19500.00"), response.getAmount());
    }

    @Test
    @DisplayName("Should prevent duplicate payments if booking is already PAID")
    void testPreventDuplicatePayment() {
        PaymentRequest request = new PaymentRequest(10, "Card", true);

        when(bookingRepository.findById(10)).thenReturn(Optional.of(sampleBooking));
        when(paymentRepository.existsByBooking_BookingIdAndPaymentStatus(10, "PAID")).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                paymentService.processPayment(request));

        assertTrue(ex.getMessage().contains("already been marked as PAID"));
    }

    @Test
    @DisplayName("Should handle simulated failure without producing a PAID record")
    void testSimulatedPaymentFailure() {
        PaymentRequest request = new PaymentRequest(10, "Card", false);

        when(bookingRepository.findById(10)).thenReturn(Optional.of(sampleBooking));
        when(paymentRepository.existsByBooking_BookingIdAndPaymentStatus(10, "PAID")).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setPaymentId(502);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("PENDING", response.getPaymentStatus());
        assertTrue(response.getMessage().contains("FAILED"));
    }

    @Test
    @DisplayName("Should reject invalid payment method")
    void testInvalidPaymentMethodRejected() {
        PaymentRequest request = new PaymentRequest(10, "BitCoin", true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                paymentService.processPayment(request));

        assertTrue(ex.getMessage().contains("Invalid payment method"));
    }

    @Test
    @DisplayName("Should reject payment for non-existent booking ID")
    void testNonExistentBookingRejected() {
        PaymentRequest request = new PaymentRequest(999, "Cash", true);

        when(bookingRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                paymentService.processPayment(request));

        assertTrue(ex.getMessage().contains("Booking not found"));
    }

    @Test
    @DisplayName("Cash payment should always be saved as PENDING regardless of simulateSuccess")
    void testCashPaymentAlwaysPending() {
        PaymentRequest request = new PaymentRequest(10, "Cash", true); // simulateSuccess=true ignored for Cash

        when(bookingRepository.findById(10)).thenReturn(Optional.of(sampleBooking));
        when(paymentRepository.existsByBooking_BookingIdAndPaymentStatus(10, "PAID")).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setPaymentId(503);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        // success=true so the frontend shows the receipt page
        assertTrue(response.isSuccess());
        // paymentStatus must be PENDING — guest pays at front desk
        assertEquals("PENDING", response.getPaymentStatus());
        assertEquals("Cash", response.getPaymentMethod());
        // Amount must match the booking's database total (server-authoritative)
        assertEquals(new BigDecimal("19500.00"), response.getAmount());
        // Message should mention PENDING / front desk / check-in
        assertTrue(response.getMessage().contains("PENDING") || response.getMessage().contains("front desk"));
    }
}
