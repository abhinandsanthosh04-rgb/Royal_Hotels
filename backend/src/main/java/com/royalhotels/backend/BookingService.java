package com.royalhotels.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BookingService - Business Logic Layer for hotel bookings.
 *
 * OOP Concepts Demonstrated:
 * - Encapsulation of booking lifecycle and date validation rules.
 * - Backend authority: Number of nights and total price are ALWAYS computed
 *   using the database room price. No client-submitted price is trusted.
 */
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Create a real hotel booking in MySQL.
     *
     * @param request BookingRequest containing customer details, room ID, dates, and guests
     * @return BookingResponse with confirmed booking details
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // 1. Validate mandatory input fields
        if (request == null) {
            throw new IllegalArgumentException("Booking request body cannot be null.");
        }
        if (request.getRoomId() == null) {
            throw new IllegalArgumentException("Room ID is required.");
        }
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required.");
        }
        if (request.getCustomerPhone() == null || request.getCustomerPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer phone number is required.");
        }
        if (request.getCheckIn() == null || request.getCheckOut() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (request.getNumberOfGuests() == null || request.getNumberOfGuests() <= 0) {
            throw new IllegalArgumentException("Number of guests must be at least 1.");
        }

        // 2. Validate dates: checkIn < checkOut, and checkIn not in past
        if (!request.getCheckIn().isBefore(request.getCheckOut())) {
            throw new IllegalArgumentException("Check-out date must be strictly after check-in date.");
        }
        if (request.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        // 3. Verify room exists and check capacity & maintenance status
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + request.getRoomId()));

        if ("MAINTENANCE".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is currently under maintenance.");
        }
        if (room.getCapacity() < request.getNumberOfGuests()) {
            throw new IllegalArgumentException("Room " + room.getRoomNumber() +
                    " capacity (" + room.getCapacity() + " guests) cannot accommodate " +
                    request.getNumberOfGuests() + " guests.");
        }

        // 4. Overlap verification: ensure room is not already booked for overlapping dates
        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                room.getRoomId(), request.getCheckIn(), request.getCheckOut());
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Room " + room.getRoomNumber() +
                    " is already reserved for overlapping dates by an existing confirmed booking.");
        }

        // 5. Backend calculation: calculate nights and total amount strictly using DB price
        int numberOfNights = (int) ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));

        // 6. Find existing customer or register new customer profile
        String cleanEmail = request.getCustomerEmail().trim().toLowerCase();
        Customer customer = customerRepository.findByEmail(cleanEmail)
                .orElseGet(() -> {
                    Customer newCustomer = new Customer(
                            request.getCustomerName().trim(),
                            request.getCustomerPhone().trim(),
                            cleanEmail,
                            request.getCustomerAddress(),
                            request.getCustomerIdProof()
                    );
                    return customerRepository.save(newCustomer);
                });

        // 7. Instantiate and persist Booking entity
        Booking booking = new Booking(
                customer,
                room,
                request.getCheckIn(),
                request.getCheckOut(),
                request.getNumberOfGuests(),
                numberOfNights,
                totalAmount
        );
        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(savedBooking);
    }

    /**
     * Fetch a booking by its primary key ID.
     */
    public Optional<BookingResponse> getBookingById(Integer bookingId) {
        return bookingRepository.findById(bookingId).map(BookingResponse::new);
    }

    /**
     * Fetch all bookings.
     */
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
    }
}
