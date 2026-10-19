package com.royalhotels.backend;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PaymentResponse - DTO returned upon payment processing.
 *
 * OOP Concept: Encapsulation. Returns payment and booking context
 * in a clean shape for the Angular receipt view.
 */
public class PaymentResponse {

    private Integer paymentId;
    private Integer bookingId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String roomNumber;
    private String roomType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numberOfNights;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDate paymentDate;
    private boolean success;
    private String message;

    public PaymentResponse() {
    }

    public PaymentResponse(Payment payment, boolean success, String message) {
        if (payment != null) {
            this.paymentId = payment.getPaymentId();
            this.amount = payment.getAmount();
            this.paymentMethod = payment.getPaymentMethod();
            this.paymentStatus = payment.getPaymentStatus();
            this.paymentDate = payment.getPaymentDate();

            Booking b = payment.getBooking();
            if (b != null) {
                this.bookingId = b.getBookingId();
                this.checkIn = b.getCheckIn();
                this.checkOut = b.getCheckOut();
                this.numberOfNights = b.getNumberOfNights();

                if (b.getCustomer() != null) {
                    this.customerName = b.getCustomer().getName();
                    this.customerEmail = b.getCustomer().getEmail();
                    this.customerPhone = b.getCustomer().getPhone();
                }
                if (b.getRoom() != null) {
                    this.roomNumber = b.getRoom().getRoomNumber();
                    this.roomType = b.getRoom().getRoomType();
                }
            }
        }
        this.success = success;
        this.message = message;
    }

    // Static factory for failed simulated attempts without creating a PAID payment
    // record
    public static PaymentResponse failure(Integer bookingId, BigDecimal amount, String paymentMethod, String message) {
        PaymentResponse resp = new PaymentResponse();
        resp.setBookingId(bookingId);
        resp.setAmount(amount);
        resp.setPaymentMethod(paymentMethod);
        resp.setPaymentStatus("PENDING");
        resp.setPaymentDate(LocalDate.now());
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    // ─── Getters and Setters ─────────────────────────────────────────────────

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
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

    public Integer getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(Integer numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
