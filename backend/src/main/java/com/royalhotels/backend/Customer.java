package com.royalhotels.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Customer - JPA Entity representing the 'customers' table in MySQL.
 *
 * OOP Concept: Encapsulation. All fields are private and accessed through
 * public getters and setters. This class models a real-world hotel guest.
 */
@Entity
@Table(name = "customers")
public class Customer {

    // PRIMARY KEY - auto-incremented by MySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    // Guest's full name, e.g. "Vikramaditya Rathore"
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // Contact telephone number
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    // Contact email address, must be unique across all customers
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    // Physical postal address (optional)
    @Column(name = "address", length = 255)
    private String address;

    // Identity proof number (e.g. Aadhaar, Passport) (optional)
    @Column(name = "id_proof", length = 100)
    private String idProof;

    // Timestamp when the customer profile was created
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ─── Constructors ────────────────────────────────────────────────────────

    // Default no-argument constructor (required by JPA specification)
    public Customer() {
    }

    // Parameterized constructor for convenient object initialization
    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    public Customer(String name, String phone, String email, String address, String idProof) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.idProof = idProof;
        this.createdAt = LocalDateTime.now();
    }

    // Automatically set createdAt before saving if not already set
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ─── Getters and Setters ─────────────────────────────────────────────────

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
