package com.royalhotels.backend;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * AdminStatsResponse - High-level executive statistics for the Admin Overview tab.
 */
public class AdminStatsResponse {

    private long totalBookings;
    private long confirmedBookings;
    private long checkedInBookings;
    private long checkedOutBookings;
    private long cancelledBookings;

    private BigDecimal totalRevenue;
    private long totalCustomers;
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private long maintenanceRooms;
    private double occupancyRatePercentage;

    private Map<String, BigDecimal> revenueByRoomType;
    private List<AdminBookingItem> recentBookings;
    private List<AdminPaymentItem> recentPayments;

    public AdminStatsResponse() {
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getConfirmedBookings() {
        return confirmedBookings;
    }

    public void setConfirmedBookings(long confirmedBookings) {
        this.confirmedBookings = confirmedBookings;
    }

    public long getCheckedInBookings() {
        return checkedInBookings;
    }

    public void setCheckedInBookings(long checkedInBookings) {
        this.checkedInBookings = checkedInBookings;
    }

    public long getCheckedOutBookings() {
        return checkedOutBookings;
    }

    public void setCheckedOutBookings(long checkedOutBookings) {
        this.checkedOutBookings = checkedOutBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public long getMaintenanceRooms() {
        return maintenanceRooms;
    }

    public void setMaintenanceRooms(long maintenanceRooms) {
        this.maintenanceRooms = maintenanceRooms;
    }

    public double getOccupancyRatePercentage() {
        return occupancyRatePercentage;
    }

    public void setOccupancyRatePercentage(double occupancyRatePercentage) {
        this.occupancyRatePercentage = occupancyRatePercentage;
    }

    public Map<String, BigDecimal> getRevenueByRoomType() {
        return revenueByRoomType;
    }

    public void setRevenueByRoomType(Map<String, BigDecimal> revenueByRoomType) {
        this.revenueByRoomType = revenueByRoomType;
    }

    public List<AdminBookingItem> getRecentBookings() {
        return recentBookings;
    }

    public void setRecentBookings(List<AdminBookingItem> recentBookings) {
        this.recentBookings = recentBookings;
    }

    public List<AdminPaymentItem> getRecentPayments() {
        return recentPayments;
    }

    public void setRecentPayments(List<AdminPaymentItem> recentPayments) {
        this.recentPayments = recentPayments;
    }
}
