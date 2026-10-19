package com.royalhotels.backend;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * BookingResponse - DTO returned upon successful booking creation or retrieval.
 *
 * OOP Concept: Encapsulation. Returns booking, customer, and room data in a
 * clean, flat structure for API clients.
 */
public class BookingResponse {

    private Integer bookingId;
    private Integer customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Integer roomId;
    private String roomNumber;
    private String roomType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numberOfGuests;
    private Integer numberOfNights;
    private BigDecimal pricePerNight;
    private BigDecimal totalAmount;
    private String bookingStatus;
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(Booking booking) {
        this.bookingId = booking.getBookingId();
        if (booking.getCustomer() != null) {
            this.customerId = booking.getCustomer().getCustomerId();
            this.customerName = booking.getCustomer().getName();
            this.customerEmail = booking.getCustomer().getEmail();
            this.customerPhone = booking.getCustomer().getPhone();
        }
        if (booking.getRoom() != null) {
            this.roomId = booking.getRoom().getRoomId();
            this.roomNumber = booking.getRoom().getRoomNumber();
            this.roomType = booking.getRoom().getRoomType();
            this.pricePerNight = booking.getRoom().getPricePerNight();
        }
        this.checkIn = booking.getCheckIn();
        this.checkOut = booking.getCheckOut();
        this.numberOfGuests = booking.getNumberOfGuests();
        this.numberOfNights = booking.getNumberOfNights();
        this.totalAmount = booking.getTotalAmount();
        this.bookingStatus = booking.getBookingStatus();
        this.createdAt = booking.getCreatedAt();
    }

    // ─── Getters and Setters ─────────────────────────────────────────────────

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
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

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
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
