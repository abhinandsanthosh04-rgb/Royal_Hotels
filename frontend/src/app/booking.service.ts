import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * BookingRequest - Exact payload expected by the Java Spring Boot backend (BookingRequest.java).
 *
 * Notice: NO total amount or price is included here. The Java backend computes
 * the total amount using the room's database price to prevent client-side tampering.
 */
export interface BookingRequest {
  roomId: number;
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  customerAddress?: string;
  customerIdProof?: string;
  checkIn: string; // YYYY-MM-DD
  checkOut: string; // YYYY-MM-DD
  numberOfGuests: number;
}

/**
 * BookingResponse - Exact response returned by the Java backend (BookingResponse.java).
 */
export interface BookingResponse {
  bookingId: number;
  customerId: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  roomId: number;
  roomNumber: string;
  roomType: string;
  checkIn: string;
  checkOut: string;
  numberOfGuests: number;
  numberOfNights: number;
  pricePerNight: number;
  totalAmount: number;
  bookingStatus: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private readonly apiBase = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  /**
   * Submit a new reservation to the Java Spring Boot backend.
   * Calls: POST http://localhost:8080/api/bookings
   */
  createBooking(request: BookingRequest): Observable<BookingResponse> {
    return this.http.post<BookingResponse>(`${this.apiBase}/bookings`, request);
  }

  /**
   * Retrieve a booking by its primary key ID.
   * Calls: GET http://localhost:8080/api/bookings/{id}
   */
  getBookingById(bookingId: number): Observable<BookingResponse> {
    return this.http.get<BookingResponse>(`${this.apiBase}/bookings/${bookingId}`);
  }
}
