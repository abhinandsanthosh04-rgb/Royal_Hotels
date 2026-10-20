import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface PaymentRequest {
  bookingId: number;
  paymentMethod: 'UPI' | 'Card' | 'Cash' | string;
  simulateSuccess: boolean;
}

export interface PaymentResponse {
  paymentId: number;
  bookingId: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  roomNumber: string;
  roomType: string;
  checkIn: string;
  checkOut: string;
  numberOfNights: number;
  amount: number;
  paymentMethod: string;
  paymentStatus: string; // 'PAID' | 'PENDING'
  paymentDate: string;
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private readonly apiBase = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Process a demo payment for an existing booking.
   * Calls: POST /api/payments
   */
  processPayment(request: PaymentRequest): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiBase}/payments`, request);
  }

  /**
   * Retrieve payment records for a booking.
   * Calls: GET /api/payments/booking/{bookingId}
   */
  getPaymentsByBookingId(bookingId: number): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(`${this.apiBase}/payments/booking/${bookingId}`);
  }
}
