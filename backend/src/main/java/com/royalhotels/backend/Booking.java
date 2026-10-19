package com.royalhotels.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Booking - JPA Entity representing the 'bookings' table in MySQL.
 *
 * OOP Concept: Composition and Association. A Booking associates a Customer
 * and a Room for a specific date range [checkIn, checkOut).
 */
@Entity
@Table(name = "bookings")
public class Booking {

    // PRIMARY KEY - auto-incremented by MySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    // Associated Customer (Foreign Key: customer_id)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Associated Room (Foreign Key: room_id)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Check-in date (inclusive)
    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    // Check-out date (exclusive)
    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    // Number of guests staying in the room (MySQL TINYINT)
    @Column(name = "number_of_guests", nullable = false, columnDefinition = "TINYINT")
    private Integer numberOfGuests;

    // Number of nights calculated as days between checkIn and checkOut (MySQL SMALLINT)
    @Column(name = "number_of_nights", nullable = false, columnDefinition = "SMALLINT")
    private Integer numberOfNights;

    // Total booking amount calculated strictly on backend (pricePerNight * numberOfNights)
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Booking lifecycle status: CONFIRMED | CHECKED_IN | CHECKED_OUT | CANCELLED
    @Column(name = "booking_status", nullable = false,
            columnDefinition = "ENUM('CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED')")
    private String bookingStatus;

    // Timestamp when the booking was made
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ─── Constructors ────────────────────────────────────────────────────────

    // Default no-argument constructor (required by JPA)
    public Booking() {
    }

    // Parameterized constructor
    public Booking(Customer customer, Room room, LocalDate checkIn, LocalDate checkOut,
                   Integer numberOfGuests, Integer numberOfNights, BigDecimal totalAmount) {
        this.customer = customer;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.numberOfNights = numberOfNights;
        this.totalAmount = totalAmount;
        this.bookingStatus = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.bookingStatus == null) {
            this.bookingStatus = "CONFIRMED";
        }
    }

    // ─── Getters and Setters ─────────────────────────────────────────────────

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public Integer getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(Integer numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
