package com.royalhotels.backend;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * AvailableRoomResponse - DTO returned when querying room availability.
 *
 * OOP Concept: Data Transfer Object (DTO) pattern & Encapsulation.
 * Combines Room entity data with dynamic calculation results (numberOfNights, totalPrice)
 * computed strictly on the backend using the room's database price.
 */
public class AvailableRoomResponse {

    private Integer roomId;
    private String roomNumber;
    private String roomType;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private String status;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numberOfNights;
    private BigDecimal totalPrice;

    // Default constructor for Jackson JSON serialization
    public AvailableRoomResponse() {
    }

    // Parameterized constructor initializing all fields
    public AvailableRoomResponse(Room room, LocalDate checkIn, LocalDate checkOut,
                                 int numberOfNights, BigDecimal totalPrice) {
        this.roomId = room.getRoomId();
        this.roomNumber = room.getRoomNumber();
        this.roomType = room.getRoomType();
        this.pricePerNight = room.getPricePerNight();
        this.capacity = room.getCapacity();
        this.status = room.getStatus();
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfNights = numberOfNights;
        this.totalPrice = totalPrice;
    }

    // ─── Getters and Setters ─────────────────────────────────────────────────

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

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
