package com.royalhotels.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * AdminDashboardController - REST API Controller for authorized administrator dashboard actions.
 *
 * Security: Every endpoint here requires a valid Bearer JWT token with ROLE_ADMIN.
 * Any unauthenticated or unauthorized access is rejected with HTTP 401 / 403.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    /**
     * GET /api/admin/stats
     * Live executive metrics, revenue sums, occupancy rate, room status breakdown, and recent activity.
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminDashboardService.getStats());
    }

    /**
     * GET /api/admin/bookings
     * Paginated and searchable reservations.
     * Params: search, status, page, size
     */
    @GetMapping("/bookings")
    public ResponseEntity<PageResponse<AdminBookingItem>> getBookings(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(adminDashboardService.getBookings(search, status, page, size));
    }

    /**
     * PUT /api/admin/bookings/{id}/status
     * Updates reservation status (CONFIRMED -> CHECKED_IN -> CHECKED_OUT or CANCELLED)
     * and synchronizes room occupancy.
     */
    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {
        try {
            String newStatus = payload != null ? payload.get("status") : null;
            AdminBookingItem updated = adminDashboardService.updateBookingStatus(id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /api/admin/customers
     * Paginated customer profiles with total bookings count and lifetime spend.
     */
    @GetMapping("/customers")
    public ResponseEntity<PageResponse<AdminCustomerItem>> getCustomers(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(adminDashboardService.getCustomers(search, page, size));
    }

    /**
     * GET /api/admin/rooms
     * Paginated hotel rooms catalogue with filters.
     */
    @GetMapping("/rooms")
    public ResponseEntity<PageResponse<Room>> getRooms(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(adminDashboardService.getRooms(search, type, status, page, size));
    }

    /**
     * PUT /api/admin/rooms/{id}/status
     * Updates physical room status (AVAILABLE, BOOKED, OCCUPIED, MAINTENANCE).
     */
    @PutMapping("/rooms/{id}/status")
    public ResponseEntity<?> updateRoomStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {
        try {
            String newStatus = payload != null ? payload.get("status") : null;
            Room updated = adminDashboardService.updateRoomStatus(id, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/rooms/{id}/price
     * Updates room price per night.
     */
    @PutMapping("/rooms/{id}/price")
    public ResponseEntity<?> updateRoomPrice(
            @PathVariable Integer id,
            @RequestBody Map<String, BigDecimal> payload) {
        try {
            BigDecimal newPrice = payload != null ? payload.get("pricePerNight") : null;
            Room updated = adminDashboardService.updateRoomPrice(id, newPrice);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /api/admin/payments
     * Paginated payment transaction history.
     */
    @GetMapping("/payments")
    public ResponseEntity<PageResponse<AdminPaymentItem>> getPayments(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "method", required = false) String method,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(adminDashboardService.getPayments(search, status, method, page, size));
    }
}
