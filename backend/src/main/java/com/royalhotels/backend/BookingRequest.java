package com.royalhotels.backend;

import java.time.LocalDate;

/**
 * BookingRequest - Request payload for creating a real hotel booking.
 *
 * OOP Concept: Security and Encapsulation. Notice there is NO total price or
 * price per night field here. Pricing is calculated strictly on the backend
 * by querying the database room record, preventing any client-side price tampering.
 */
public class BookingRequest {

    private Integer roomId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private String customerIdProof;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numberOfGuests;

    public BookingRequest() {
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerIdProof() {
        return customerIdProof;
    }

    public void setCustomerIdProof(String customerIdProof) {
        this.customerIdProof = customerIdProof;
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
}
