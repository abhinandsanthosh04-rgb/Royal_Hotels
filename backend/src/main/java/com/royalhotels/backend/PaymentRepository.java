package com.royalhotels.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * PaymentRepository - Data Access Layer for the 'payments' table.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByBooking_BookingId(Integer bookingId);

    Optional<Payment> findFirstByBooking_BookingIdOrderByPaymentIdDesc(Integer bookingId);

    boolean existsByBooking_BookingIdAndPaymentStatus(Integer bookingId, String paymentStatus);
}
