package com.royalhotels.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * PaymentController - REST API Controller for hotel demo payments.
 *
 * OOP Concept: Controller Layer in MVC architecture.
 * Exposes payment processing endpoints for the Angular checkout flow.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * POST /api/payments
     *
     * Processes a demo payment for an existing booking.
     * Uses the backend-verified booking amount, prevents duplicates,
     * and persists the payment in the 'payments' table.
     */
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.processPayment(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                // Simulated failure: returned with HTTP 200 containing failure message
                // so the Angular frontend can display the demo decline cleanly without crashing
                return ResponseEntity.ok(response);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Payment processing error: " + e.getMessage()));
        }
    }

    /**
     * GET /api/payments/booking/{bookingId}
     * Retrieves all payment transactions recorded for a given booking ID.
     */
    @GetMapping("/booking/{bookingId}")
    public List<PaymentResponse> getPaymentsForBooking(@PathVariable Integer bookingId) {
        return paymentService.getPaymentsByBookingId(bookingId);
    }

    /**
     * GET /api/payments/booking/{bookingId}/latest
     * Retrieves the latest payment record for a given booking ID.
     */
    @GetMapping("/booking/{bookingId}/latest")
    public ResponseEntity<?> getLatestPaymentForBooking(@PathVariable Integer bookingId) {
        return paymentService.getLatestPaymentByBookingId(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
