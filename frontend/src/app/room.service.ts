import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * ApiRoom - matches the JSON shape returned by the Java backend.
 *
 * The Java Room entity serialises to these exact field names
 * (Jackson uses camelCase from the getter names by default):
 *   roomId, roomNumber, roomType, pricePerNight, capacity, status, createdAt
 */
export interface ApiRoom {
  roomId: number;
  roomNumber: string;
  roomType: string;       // 'Standard' | 'Deluxe' | 'Suite'
  pricePerNight: number;
  capacity: number;
  status: string;         // 'AVAILABLE' | 'BOOKED' | 'OCCUPIED' | 'MAINTENANCE'
  createdAt: string;
}

/**
 * AvailableRoomResponse - matches AvailableRoomResponse.java returned by the
 * dynamic date-based availability endpoint.
 *
 * GET /api/rooms/available?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD&guests=N
 */
export interface AvailableRoomResponse {
  roomId: number;
  roomNumber: string;
  roomType: string;
  pricePerNight: number;
  capacity: number;
  status: string;
  createdAt: string;      // included so this type is compatible with ApiRoom
  checkIn: string;        // YYYY-MM-DD
  checkOut: string;       // YYYY-MM-DD
  numberOfNights: number;
  totalPrice: number;
}

/**
 * RoomService - communicates with the Java Spring Boot backend.
 *
 * It uses Angular's HttpClient to make HTTP GET requests.
 * The backend must be running at http://localhost:8080 for these to work.
 */
@Injectable({
  providedIn: 'root'
})
export class RoomService {

  /** Base URL of the Java Spring Boot backend */
  private readonly apiBase = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  /**
   * Fetch ALL rooms from the database regardless of status.
   * Calls: GET http://localhost:8080/api/rooms
   */
  getAllRooms(): Observable<ApiRoom[]> {
    return this.http.get<ApiRoom[]>(`${this.apiBase}/rooms`);
  }

  /**
   * Fetch only AVAILABLE rooms from the database.
   * Calls: GET http://localhost:8080/api/rooms/available
   */
  getAvailableRooms(): Observable<ApiRoom[]> {
    return this.http.get<ApiRoom[]>(`${this.apiBase}/rooms/available`);
  }

  /**
   * Check dynamic date-based availability from MySQL.
   * Calls: GET /api/rooms/available?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD&guests=N
   *
   * The backend runs the SQL overlap query and returns only truly available
   * rooms for the requested date range, with backend-calculated nights and
   * total price.  This is the endpoint that should be called when the user
   * selects check-in / check-out dates and clicks "CHECK AVAILABILITY".
   *
   * @param checkIn  YYYY-MM-DD string
   * @param checkOut YYYY-MM-DD string
   * @param guests   number of guests (>= 1)
   */
  checkAvailability(checkIn: string, checkOut: string, guests: number): Observable<AvailableRoomResponse[]> {
    const params = new HttpParams()
      .set('checkIn', checkIn)
      .set('checkOut', checkOut)
      .set('guests', guests.toString());
    return this.http.get<AvailableRoomResponse[]>(`${this.apiBase}/rooms/available`, { params });
  }
}
