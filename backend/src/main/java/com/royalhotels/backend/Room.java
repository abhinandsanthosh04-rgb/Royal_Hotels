package com.royalhotels.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Room - JPA Entity that maps to the 'rooms' table in MySQL.
 *
 * OOP Concept: This is an Entity class. Each Room object represents
 * one row in the 'rooms' table. JPA (Java Persistence API) handles
 * saving and loading this object from the database automatically.
 *
 * The column names here EXACTLY match the 'rooms' table columns:
 *   room_id, room_number, room_type, price_per_night,
 *   capacity, status, created_at
 */
@Entity
@Table(name = "rooms")
public class Room {

    // PRIMARY KEY - auto-incremented by MySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    // Room number like "101", "201", "301"
    @Column(name = "room_number", nullable = false, unique = true, length = 10)
    private String roomNumber;

    // Room category: Standard, Deluxe, or Suite  (MySQL ENUM)
    // MySQL ENUM is reported to JDBC as Types#CHAR, not VARCHAR.
    // columnDefinition tells Hibernate's validator to expect CHAR, not VARCHAR(255).
    @Column(name = "room_type", nullable = false,
            columnDefinition = "ENUM('Standard','Deluxe','Suite')")
    private String roomType;

    // Price in Indian Rupees, e.g. 3500.00
    @Column(name = "price_per_night", nullable = false)
    private BigDecimal pricePerNight;

    // Maximum number of guests allowed in the room.
    // MySQL stores this as TINYINT, so we tell Hibernate exactly that via
    // columnDefinition. The Java type stays Integer for convenience.
    @Column(name = "capacity", nullable = false, columnDefinition = "TINYINT")
    private Integer capacity;

    // Current room status: AVAILABLE | BOOKED | OCCUPIED | MAINTENANCE
    // Same ENUM-to-CHAR issue as room_type; fixed here pre-emptively.
    @Column(name = "status", nullable = false,
            columnDefinition = "ENUM('AVAILABLE','BOOKED','OCCUPIED','MAINTENANCE')")
    private String status;

    // Timestamp when the room record was created
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ─── No-argument constructor (required by JPA) ───────────────────────────
    public Room() {
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    public Integer getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ─── Setters ─────────────────────────────────────────────────────────────

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
