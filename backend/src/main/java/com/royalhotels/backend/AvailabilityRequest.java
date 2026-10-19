package com.royalhotels.backend;

import java.time.LocalDate;

/**
 * AvailabilityRequest - Request payload for checking room availability.
 *
 * OOP Concept: Encapsulation. Encapsulates requested stay dates and guest count.
 */
public class AvailabilityRequest {

    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
    private Integer numberOfGuests; // Alternative field name for convenience

    public AvailabilityRequest() {
    }

    public AvailabilityRequest(LocalDate checkIn, LocalDate checkOut, Integer guests) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
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

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    /**
     * Resolves the guest count regardless of whether the caller sent
     * 'guests' or 'numberOfGuests'. Defaults to 1 if neither provided.
     */
    public Integer getEffectiveGuests() {
        if (this.guests != null) {
            return this.guests;
        }
        if (this.numberOfGuests != null) {
            return this.numberOfGuests;
        }
        return 1;
    }
}
