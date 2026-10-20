import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminAuthService } from './admin-auth.service';
import { environment } from '../../environments/environment';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface AdminStatsResponse {
  totalBookings: number;
  confirmedBookings: number;
  checkedInBookings: number;
  checkedOutBookings: number;
  cancelledBookings: number;
  totalRevenue: number;
  totalCustomers: number;
  totalRooms: number;
  availableRooms: number;
  occupiedRooms: number;
  maintenanceRooms: number;
  occupancyRatePercentage: number;
  revenueByRoomType: { [key: string]: number };
  recentBookings: AdminBookingItem[];
  recentPayments: AdminPaymentItem[];
}

export interface AdminBookingItem {
  bookingId: number;
  customerId: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  roomId: number;
  roomNumber: string;
  roomType: string;
  pricePerNight: number;
  checkIn: string;
  checkOut: string;
  numberOfGuests: number;
  numberOfNights: number;
  totalAmount: number;
  bookingStatus: 'CONFIRMED' | 'CHECKED_IN' | 'CHECKED_OUT' | 'CANCELLED';
  paymentStatus: 'PAID' | 'PENDING';
  paymentMethod: string;
  createdAt: string;
}

export interface AdminCustomerItem {
  customerId: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  idProof: string;
  totalBookings: number;
  totalSpent: number;
  createdAt: string;
}

export interface AdminRoomItem {
  roomId: number;
  roomNumber: string;
  roomType: 'Standard' | 'Deluxe' | 'Suite';
  pricePerNight: number;
  capacity: number;
  status: 'AVAILABLE' | 'BOOKED' | 'OCCUPIED' | 'MAINTENANCE';
  createdAt: string;
}

export interface AdminPaymentItem {
  paymentId: number;
  bookingId: number;
  customerName: string;
  customerEmail: string;
  roomNumber: string;
  roomType: string;
  amount: number;
  paymentMethod: string;
  paymentStatus: 'PAID' | 'PENDING';
  paymentDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminApiService {
  private readonly baseUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient, private authService: AdminAuthService) {}

  getStats(): Observable<AdminStatsResponse> {
    return this.http.get<AdminStatsResponse>(`${this.baseUrl}/stats`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getBookings(search?: string, status?: string, page: number = 0, size: number = 10): Observable<PageResponse<AdminBookingItem>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }

    return this.http.get<PageResponse<AdminBookingItem>>(`${this.baseUrl}/bookings`, {
      headers: this.authService.getAuthHeaders(),
      params
    });
  }

  updateBookingStatus(bookingId: number, status: string): Observable<AdminBookingItem> {
    return this.http.put<AdminBookingItem>(`${this.baseUrl}/bookings/${bookingId}/status`, { status }, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getCustomers(search?: string, page: number = 0, size: number = 10): Observable<PageResponse<AdminCustomerItem>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }

    return this.http.get<PageResponse<AdminCustomerItem>>(`${this.baseUrl}/customers`, {
      headers: this.authService.getAuthHeaders(),
      params
    });
  }

  getRooms(search?: string, type?: string, status?: string, page: number = 0, size: number = 10): Observable<PageResponse<AdminRoomItem>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (type && type !== 'ALL') {
      params = params.set('type', type);
    }
    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }

    return this.http.get<PageResponse<AdminRoomItem>>(`${this.baseUrl}/rooms`, {
      headers: this.authService.getAuthHeaders(),
      params
    });
  }

  updateRoomStatus(roomId: number, status: string): Observable<AdminRoomItem> {
    return this.http.put<AdminRoomItem>(`${this.baseUrl}/rooms/${roomId}/status`, { status }, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateRoomPrice(roomId: number, pricePerNight: number): Observable<AdminRoomItem> {
    return this.http.put<AdminRoomItem>(`${this.baseUrl}/rooms/${roomId}/price`, { pricePerNight }, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getPayments(search?: string, status?: string, method?: string, page: number = 0, size: number = 10): Observable<PageResponse<AdminPaymentItem>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }
    if (method && method !== 'ALL') {
      params = params.set('method', method);
    }

    return this.http.get<PageResponse<AdminPaymentItem>>(`${this.baseUrl}/payments`, {
      headers: this.authService.getAuthHeaders(),
      params
    });
  }
}
