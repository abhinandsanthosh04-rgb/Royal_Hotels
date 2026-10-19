package com.royalhotels.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payment - JPA Entity representing the 'payments' table in MySQL.
 *
 * OOP Concept: Encapsulation. Maps transaction details associated
 * with a confirmed hotel booking.
 */
@Entity
@Table(name = "payments")
public class Payment {

    // PRIMARY KEY - auto-incremented by MySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    // Associated Booking (Foreign Key: booking_id)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Transaction amount
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // Payment method: Cash | UPI | Card
    @Column(name = "payment_method", nullable = false,
            columnDefinition = "ENUM('Cash','UPI','Card')")
    private String paymentMethod;

    // Payment status: PAID | PENDING
    @Column(name = "payment_status", nullable = false,
            columnDefinition = "ENUM('PAID','PENDING')")
    private String paymentStatus;

    // Date when payment occurred
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    // ─── Constructors ────────────────────────────────────────────────────────

    public Payment() {
    }

    public Payment(Booking booking, BigDecimal amount, String paymentMethod, String paymentStatus, LocalDate paymentDate) {
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    // ─── Getters and Setters ─────────────────────────────────────────────────

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
