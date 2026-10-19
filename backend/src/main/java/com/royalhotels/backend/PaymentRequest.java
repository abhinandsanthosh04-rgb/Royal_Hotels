package com.royalhotels.backend;

/**
 * PaymentRequest - DTO for processing a demo payment.
 *
 * OOP Concept: Security and Encapsulation.
 * Notice: This request does NOT accept an amount from the client.
 * The server looks up the confirmed booking in MySQL and retrieves the
 * verified total_amount directly, preventing any client-side tampering.
 */
public class PaymentRequest {

    private Integer bookingId;
    private String paymentMethod;     // 'Cash' | 'UPI' | 'Card'
    private Boolean simulateSuccess;  // true for simulated success, false for simulated failure

    public PaymentRequest() {
    }

    public PaymentRequest(Integer bookingId, String paymentMethod, Boolean simulateSuccess) {
        this.bookingId = bookingId;
        this.paymentMethod = paymentMethod;
        this.simulateSuccess = simulateSuccess;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getSimulateSuccess() {
        return simulateSuccess != null ? simulateSuccess : true;
    }

    public void setSimulateSuccess(Boolean simulateSuccess) {
        this.simulateSuccess = simulateSuccess;
    }
}
