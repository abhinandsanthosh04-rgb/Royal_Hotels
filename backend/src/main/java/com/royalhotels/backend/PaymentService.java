package com.royalhotels.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PaymentService - Business Logic Layer for hotel payments.
 *
 * OOP Concepts Demonstrated:
 * - Separation of Concerns: Payment business rules, duplicate validation,
 *   and database amount resolution live strictly in the service layer.
 * - Security Authority: Total payment amount is ALWAYS resolved from the MySQL
 *   booking entity. No client-supplied amount is trusted.
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Process a demo payment for an existing booking.
     *
     * @param request PaymentRequest containing bookingId, paymentMethod, and simulateSuccess
     * @return PaymentResponse containing the transaction details and receipt data
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // 1. Validate request payload
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null.");
        }
        if (request.getBookingId() == null) {
            throw new IllegalArgumentException("Booking ID is required.");
        }
        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required (UPI, Card, or Cash).");
        }

        // 2. Normalize and validate payment method against MySQL ENUM('Cash','UPI','Card')
        String rawMethod = request.getPaymentMethod().trim();
        String paymentMethod;
        if (rawMethod.equalsIgnoreCase("UPI")) {
            paymentMethod = "UPI";
        } else if (rawMethod.equalsIgnoreCase("Card")) {
            paymentMethod = "Card";
        } else if (rawMethod.equalsIgnoreCase("Cash")) {
            paymentMethod = "Cash";
        } else {
            throw new IllegalArgumentException("Invalid payment method: '" + rawMethod + "'. Must be UPI, Card, or Cash.");
        }

        // 3. Look up confirmed booking in MySQL
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + request.getBookingId()));

        // 4. Duplicate payment protection: check if booking already has a PAID record
        boolean alreadyPaid = paymentRepository.existsByBooking_BookingIdAndPaymentStatus(booking.getBookingId(), "PAID");
        if (alreadyPaid) {
            throw new IllegalStateException("Booking #" + booking.getBookingId() +
                    " has already been marked as PAID. Duplicate payment cannot be processed.");
        }

        // 5. Total amount resolved strictly from database booking record
        BigDecimal verifiedAmount = booking.getTotalAmount();

        // 6. Cash at Hotel: always PENDING — guest pays at front desk upon arrival
        if ("Cash".equals(paymentMethod)) {
            Payment cashPayment = new Payment(booking, verifiedAmount, paymentMethod, "PENDING", LocalDate.now());
            Payment savedCash = paymentRepository.save(cashPayment);
            // success=true so the frontend shows the receipt page; paymentStatus=PENDING makes the stamp correct
            return new PaymentResponse(savedCash, true,
                    "Cash payment registered as PENDING. Please settle \u20b9" + verifiedAmount +
                    " at the hotel front desk upon check-in. Your reservation remains CONFIRMED.");
        }

        // 7. Handle simulated failure (UPI / Card only)
        if (!Boolean.TRUE.equals(request.getSimulateSuccess())) {
            // Simulated failure: do NOT produce a PAID payment record
            Payment failedPayment = new Payment(booking, verifiedAmount, paymentMethod, "PENDING", LocalDate.now());
            Payment saved = paymentRepository.save(failedPayment);
            PaymentResponse response = new PaymentResponse(saved, false,
                    "Demo payment was simulated as FAILED. No payment was charged. You may retry or select another payment method.");
            response.setSuccess(false);
            return response;
        }

        // 8. Handle simulated success (UPI / Card): record payment as PAID in MySQL
        String paymentStatus = "PAID";
        Payment payment = new Payment(booking, verifiedAmount, paymentMethod, paymentStatus, LocalDate.now());
        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentResponse(savedPayment, true,
                "Demo payment completed successfully via " + paymentMethod + ". Reservation confirmed with payment.");
    }

    /**
     * Retrieve all payment records for a specific booking.
     */
    public List<PaymentResponse> getPaymentsByBookingId(Integer bookingId) {
        return paymentRepository.findByBooking_BookingId(bookingId).stream()
                .map(p -> new PaymentResponse(p, "PAID".equalsIgnoreCase(p.getPaymentStatus()), "Payment record found."))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve the latest payment record for a booking.
     */
    public Optional<PaymentResponse> getLatestPaymentByBookingId(Integer bookingId) {
        return paymentRepository.findFirstByBooking_BookingIdOrderByPaymentIdDesc(bookingId)
                .map(p -> new PaymentResponse(p, "PAID".equalsIgnoreCase(p.getPaymentStatus()), "Latest payment record."));
    }
}
