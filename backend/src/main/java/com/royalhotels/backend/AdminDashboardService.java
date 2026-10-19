package com.royalhotels.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AdminDashboardService - Business logic layer for all Admin Dashboard operations.
 *
 * Provides real-time queries against MySQL tables (bookings, customers, rooms, payments),
 * aggregated executive statistics, search, filtering, pagination, and administrative updates.
 */
@Service
public class AdminDashboardService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Aggregates live system statistics for the Executive Overview dashboard.
     */
    public AdminStatsResponse getStats() {
        List<Booking> allBookings = bookingRepository.findAll();
        List<Customer> allCustomers = customerRepository.findAll();
        List<Room> allRooms = roomRepository.findAll();
        List<Payment> allPayments = paymentRepository.findAll();

        AdminStatsResponse stats = new AdminStatsResponse();

        // 1. Booking counts
        stats.setTotalBookings(allBookings.size());
        stats.setConfirmedBookings(allBookings.stream().filter(b -> "CONFIRMED".equalsIgnoreCase(b.getBookingStatus())).count());
        stats.setCheckedInBookings(allBookings.stream().filter(b -> "CHECKED_IN".equalsIgnoreCase(b.getBookingStatus())).count());
        stats.setCheckedOutBookings(allBookings.stream().filter(b -> "CHECKED_OUT".equalsIgnoreCase(b.getBookingStatus())).count());
        stats.setCancelledBookings(allBookings.stream().filter(b -> "CANCELLED".equalsIgnoreCase(b.getBookingStatus())).count());

        // 2. Revenue (sum of all PAID payments)
        BigDecimal totalRevenue = allPayments.stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getPaymentStatus()))
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRevenue);

        // 3. Customer & Room counts
        stats.setTotalCustomers(allCustomers.size());
        stats.setTotalRooms(allRooms.size());

        long availableRooms = allRooms.stream().filter(r -> "AVAILABLE".equalsIgnoreCase(r.getStatus())).count();
        long occupiedRooms = allRooms.stream().filter(r -> "OCCUPIED".equalsIgnoreCase(r.getStatus()) || "BOOKED".equalsIgnoreCase(r.getStatus())).count();
        long maintenanceRooms = allRooms.stream().filter(r -> "MAINTENANCE".equalsIgnoreCase(r.getStatus())).count();

        stats.setAvailableRooms(availableRooms);
        stats.setOccupiedRooms(occupiedRooms);
        stats.setMaintenanceRooms(maintenanceRooms);

        if (!allRooms.isEmpty()) {
            double rate = ((double) occupiedRooms / allRooms.size()) * 100.0;
            stats.setOccupancyRatePercentage(Math.round(rate * 10.0) / 10.0);
        } else {
            stats.setOccupancyRatePercentage(0.0);
        }

        // 4. Revenue by Room Type
        Map<String, BigDecimal> revenueByType = new HashMap<>();
        revenueByType.put("Standard", BigDecimal.ZERO);
        revenueByType.put("Deluxe", BigDecimal.ZERO);
        revenueByType.put("Suite", BigDecimal.ZERO);

        for (Payment p : allPayments) {
            if ("PAID".equalsIgnoreCase(p.getPaymentStatus()) && p.getBooking() != null && p.getBooking().getRoom() != null) {
                String type = p.getBooking().getRoom().getRoomType();
                BigDecimal current = revenueByType.getOrDefault(type, BigDecimal.ZERO);
                revenueByType.put(type, current.add(p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO));
            }
        }
        stats.setRevenueByRoomType(revenueByType);

        // 5. Recent 5 Bookings (sorted by ID descending)
        List<AdminBookingItem> recentBookings = allBookings.stream()
                .sorted(Comparator.comparing(Booking::getBookingId, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::mapToBookingItem)
                .collect(Collectors.toList());
        stats.setRecentBookings(recentBookings);

        // 6. Recent 5 Payments
        List<AdminPaymentItem> recentPayments = allPayments.stream()
                .sorted(Comparator.comparing(Payment::getPaymentId, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::mapToPaymentItem)
                .collect(Collectors.toList());
        stats.setRecentPayments(recentPayments);

        return stats;
    }

    /**
     * Returns paginated, searchable, and filtered bookings.
     */
    public PageResponse<AdminBookingItem> getBookings(String search, String status, int page, int size) {
        List<Booking> list = bookingRepository.findAll();

        // Filter by status if specified
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            list = list.stream()
                    .filter(b -> status.equalsIgnoreCase(b.getBookingStatus()))
                    .collect(Collectors.toList());
        }

        // Filter by search keyword (customer name, email, phone, room number, booking id)
        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            list = list.stream().filter(b -> {
                String bId = String.valueOf(b.getBookingId());
                String cName = b.getCustomer() != null ? b.getCustomer().getName().toLowerCase() : "";
                String cEmail = b.getCustomer() != null ? b.getCustomer().getEmail().toLowerCase() : "";
                String cPhone = b.getCustomer() != null ? b.getCustomer().getPhone().toLowerCase() : "";
                String rNum = b.getRoom() != null ? b.getRoom().getRoomNumber().toLowerCase() : "";
                return bId.contains(q) || cName.contains(q) || cEmail.contains(q) || cPhone.contains(q) || rNum.contains(q);
            }).collect(Collectors.toList());
        }

        // Sort by bookingId descending (newest first)
        list.sort(Comparator.comparing(Booking::getBookingId, Comparator.nullsLast(Comparator.reverseOrder())));

        int totalElements = list.size();
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int totalPages = (int) Math.ceil((double) totalElements / safeSize);

        int fromIndex = Math.min(safePage * safeSize, totalElements);
        int toIndex = Math.min(fromIndex + safeSize, totalElements);

        List<AdminBookingItem> pageContent = list.subList(fromIndex, toIndex).stream()
                .map(this::mapToBookingItem)
                .collect(Collectors.toList());

        return new PageResponse<>(pageContent, totalElements, totalPages, safePage, safeSize);
    }

    /**
     * Updates booking status (e.g., CONFIRMED -> CHECKED_IN, CHECKED_OUT, CANCELLED)
     * and synchronizes room status accordingly.
     */
    @Transactional
    public AdminBookingItem updateBookingStatus(Integer bookingId, String newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        String sanitized = newStatus != null ? newStatus.trim().toUpperCase() : "";
        if (!Arrays.asList("CONFIRMED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED").contains(sanitized)) {
            throw new IllegalArgumentException("Invalid booking status: " + newStatus);
        }

        booking.setBookingStatus(sanitized);
        Booking updated = bookingRepository.save(booking);

        // Update associated room status if appropriate
        Room room = booking.getRoom();
        if (room != null) {
            if ("CHECKED_IN".equals(sanitized)) {
                room.setStatus("OCCUPIED");
                roomRepository.save(room);
            } else if ("CHECKED_OUT".equals(sanitized) || "CANCELLED".equals(sanitized)) {
                // If checking out or cancelling, check if room has any other current CHECKED_IN booking
                List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                        room.getRoomId(), LocalDate.now(), LocalDate.now().plusDays(1));
                boolean hasOtherActive = overlaps.stream()
                        .anyMatch(b -> !b.getBookingId().equals(bookingId) && "CHECKED_IN".equalsIgnoreCase(b.getBookingStatus()));
                if (!hasOtherActive && !"MAINTENANCE".equalsIgnoreCase(room.getStatus())) {
                    room.setStatus("AVAILABLE");
                    roomRepository.save(room);
                }
            }
        }

        return mapToBookingItem(updated);
    }

    /**
     * Returns paginated and searchable customers catalogue with total bookings and spend.
     */
    public PageResponse<AdminCustomerItem> getCustomers(String search, int page, int size) {
        List<Customer> list = customerRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAll();
        List<Payment> allPayments = paymentRepository.findAll();

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            list = list.stream().filter(c -> {
                String name = c.getName() != null ? c.getName().toLowerCase() : "";
                String email = c.getEmail() != null ? c.getEmail().toLowerCase() : "";
                String phone = c.getPhone() != null ? c.getPhone().toLowerCase() : "";
                return name.contains(q) || email.contains(q) || phone.contains(q);
            }).collect(Collectors.toList());
        }

        list.sort(Comparator.comparing(Customer::getCustomerId, Comparator.nullsLast(Comparator.reverseOrder())));

        int totalElements = list.size();
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int totalPages = (int) Math.ceil((double) totalElements / safeSize);

        int fromIndex = Math.min(safePage * safeSize, totalElements);
        int toIndex = Math.min(fromIndex + safeSize, totalElements);

        List<AdminCustomerItem> pageContent = list.subList(fromIndex, toIndex).stream().map(c -> {
            AdminCustomerItem item = new AdminCustomerItem();
            item.setCustomerId(c.getCustomerId());
            item.setName(c.getName());
            item.setEmail(c.getEmail());
            item.setPhone(c.getPhone());
            item.setAddress(c.getAddress());
            item.setIdProof(c.getIdProof());
            item.setCreatedAt(c.getCreatedAt());

            long bookingCount = allBookings.stream()
                    .filter(b -> b.getCustomer() != null && c.getCustomerId().equals(b.getCustomer().getCustomerId()))
                    .count();
            item.setTotalBookings(bookingCount);

            BigDecimal spent = allPayments.stream()
                    .filter(p -> "PAID".equalsIgnoreCase(p.getPaymentStatus()) &&
                            p.getBooking() != null &&
                            p.getBooking().getCustomer() != null &&
                            c.getCustomerId().equals(p.getBooking().getCustomer().getCustomerId()))
                    .map(Payment::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setTotalSpent(spent);

            return item;
        }).collect(Collectors.toList());

        return new PageResponse<>(pageContent, totalElements, totalPages, safePage, safeSize);
    }

    /**
     * Returns paginated, searchable, and filtered rooms catalogue.
     */
    public PageResponse<Room> getRooms(String search, String type, String status, int page, int size) {
        List<Room> list = roomRepository.findAll();

        if (type != null && !type.isBlank() && !"ALL".equalsIgnoreCase(type)) {
            list = list.stream().filter(r -> type.equalsIgnoreCase(r.getRoomType())).collect(Collectors.toList());
        }

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            list = list.stream().filter(r -> status.equalsIgnoreCase(r.getStatus())).collect(Collectors.toList());
        }

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            list = list.stream().filter(r -> {
                String num = r.getRoomNumber() != null ? r.getRoomNumber().toLowerCase() : "";
                String rType = r.getRoomType() != null ? r.getRoomType().toLowerCase() : "";
                return num.contains(q) || rType.contains(q);
            }).collect(Collectors.toList());
        }

        list.sort(Comparator.comparing(Room::getRoomNumber, Comparator.nullsLast(Comparator.naturalOrder())));

        int totalElements = list.size();
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int totalPages = (int) Math.ceil((double) totalElements / safeSize);

        int fromIndex = Math.min(safePage * safeSize, totalElements);
        int toIndex = Math.min(fromIndex + safeSize, totalElements);

        return new PageResponse<>(list.subList(fromIndex, toIndex), totalElements, totalPages, safePage, safeSize);
    }

    /**
     * Updates room status (AVAILABLE, BOOKED, OCCUPIED, MAINTENANCE).
     */
    @Transactional
    public Room updateRoomStatus(Integer roomId, String newStatus) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

        String sanitized = newStatus != null ? newStatus.trim().toUpperCase() : "";
        if (!Arrays.asList("AVAILABLE", "BOOKED", "OCCUPIED", "MAINTENANCE").contains(sanitized)) {
            throw new IllegalArgumentException("Invalid room status: " + newStatus);
        }

        room.setStatus(sanitized);
        return roomRepository.save(room);
    }

    /**
     * Updates room price per night.
     */
    @Transactional
    public Room updateRoomPrice(Integer roomId, BigDecimal newPrice) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be a positive number.");
        }

        room.setPricePerNight(newPrice);
        return roomRepository.save(room);
    }

    /**
     * Returns paginated, searchable, and filtered payments transactions.
     */
    public PageResponse<AdminPaymentItem> getPayments(String search, String status, String method, int page, int size) {
        List<Payment> list = paymentRepository.findAll();

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            list = list.stream().filter(p -> status.equalsIgnoreCase(p.getPaymentStatus())).collect(Collectors.toList());
        }

        if (method != null && !method.isBlank() && !"ALL".equalsIgnoreCase(method)) {
            list = list.stream().filter(p -> method.equalsIgnoreCase(p.getPaymentMethod())).collect(Collectors.toList());
        }

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            list = list.stream().filter(p -> {
                String pId = String.valueOf(p.getPaymentId());
                String bId = p.getBooking() != null ? String.valueOf(p.getBooking().getBookingId()) : "";
                String cName = (p.getBooking() != null && p.getBooking().getCustomer() != null)
                        ? p.getBooking().getCustomer().getName().toLowerCase() : "";
                return pId.contains(q) || bId.contains(q) || cName.contains(q);
            }).collect(Collectors.toList());
        }

        list.sort(Comparator.comparing(Payment::getPaymentId, Comparator.nullsLast(Comparator.reverseOrder())));

        int totalElements = list.size();
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int totalPages = (int) Math.ceil((double) totalElements / safeSize);

        int fromIndex = Math.min(safePage * safeSize, totalElements);
        int toIndex = Math.min(fromIndex + safeSize, totalElements);

        List<AdminPaymentItem> pageContent = list.subList(fromIndex, toIndex).stream()
                .map(this::mapToPaymentItem)
                .collect(Collectors.toList());

        return new PageResponse<>(pageContent, totalElements, totalPages, safePage, safeSize);
    }

    // ── Mapping Helpers ────────────────────────────────────────────────────────

    private AdminBookingItem mapToBookingItem(Booking b) {
        AdminBookingItem item = new AdminBookingItem();
        item.setBookingId(b.getBookingId());

        if (b.getCustomer() != null) {
            item.setCustomerId(b.getCustomer().getCustomerId());
            item.setCustomerName(b.getCustomer().getName());
            item.setCustomerEmail(b.getCustomer().getEmail());
            item.setCustomerPhone(b.getCustomer().getPhone());
        }

        if (b.getRoom() != null) {
            item.setRoomId(b.getRoom().getRoomId());
            item.setRoomNumber(b.getRoom().getRoomNumber());
            item.setRoomType(b.getRoom().getRoomType());
            item.setPricePerNight(b.getRoom().getPricePerNight());
        }

        item.setCheckIn(b.getCheckIn());
        item.setCheckOut(b.getCheckOut());
        item.setNumberOfGuests(b.getNumberOfGuests());
        item.setNumberOfNights(b.getNumberOfNights());
        item.setTotalAmount(b.getTotalAmount());
        item.setBookingStatus(b.getBookingStatus());
        item.setCreatedAt(b.getCreatedAt());

        // Get latest payment for this booking
        Optional<Payment> latestPayment = paymentRepository.findFirstByBooking_BookingIdOrderByPaymentIdDesc(b.getBookingId());
        if (latestPayment.isPresent()) {
            item.setPaymentStatus(latestPayment.get().getPaymentStatus());
            item.setPaymentMethod(latestPayment.get().getPaymentMethod());
        } else {
            item.setPaymentStatus("PENDING");
            item.setPaymentMethod("-");
        }

        return item;
    }

    private AdminPaymentItem mapToPaymentItem(Payment p) {
        AdminPaymentItem item = new AdminPaymentItem();
        item.setPaymentId(p.getPaymentId());
        item.setAmount(p.getAmount());
        item.setPaymentMethod(p.getPaymentMethod());
        item.setPaymentStatus(p.getPaymentStatus());
        item.setPaymentDate(p.getPaymentDate());

        if (p.getBooking() != null) {
            item.setBookingId(p.getBooking().getBookingId());
            if (p.getBooking().getCustomer() != null) {
                item.setCustomerName(p.getBooking().getCustomer().getName());
                item.setCustomerEmail(p.getBooking().getCustomer().getEmail());
            }
            if (p.getBooking().getRoom() != null) {
                item.setRoomNumber(p.getBooking().getRoom().getRoomNumber());
                item.setRoomType(p.getBooking().getRoom().getRoomType());
            }
        }

        return item;
    }
}
