import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AdminAuthService, AdminUser } from './admin-auth.service';
import {
  AdminApiService,
  AdminStatsResponse,
  AdminBookingItem,
  AdminCustomerItem,
  AdminRoomItem,
  AdminPaymentItem,
  PageResponse
} from './admin-api.service';

type DashboardSection = 'overview' | 'reservations' | 'customers' | 'rooms' | 'payments';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  currentSection: DashboardSection = 'overview';
  isMobileSidebarOpen: boolean = false;
  currentAdmin: AdminUser | null = null;

  // Global loading and toast notification states
  isLoading: boolean = false;
  toastMessage: { text: string; type: 'success' | 'error' | 'info' } | null = null;
  toastTimeout: any = null;

  // 1. Overview & Stats
  stats: AdminStatsResponse | null = null;

  // 2. Reservations
  bookings: AdminBookingItem[] = [];
  bookingTotalElements = 0;
  bookingTotalPages = 0;
  bookingPage = 0;
  bookingSize = 10;
  bookingSearch = '';
  bookingStatusFilter = 'ALL';
  selectedBookingForStatus: AdminBookingItem | null = null;
  newBookingStatus: string = '';

  // 3. Customers
  customers: AdminCustomerItem[] = [];
  customerTotalElements = 0;
  customerTotalPages = 0;
  customerPage = 0;
  customerSize = 10;
  customerSearch = '';

  // 4. Rooms
  rooms: AdminRoomItem[] = [];
  roomTotalElements = 0;
  roomTotalPages = 0;
  roomPage = 0;
  roomSize = 10;
  roomSearch = '';
  roomTypeFilter = 'ALL';
  roomStatusFilter = 'ALL';
  editingRoom: AdminRoomItem | null = null;
  newRoomPrice: number = 0;

  // 5. Payments
  payments: AdminPaymentItem[] = [];
  paymentTotalElements = 0;
  paymentTotalPages = 0;
  paymentPage = 0;
  paymentSize = 10;
  paymentSearch = '';
  paymentStatusFilter = 'ALL';
  paymentMethodFilter = 'ALL';

  constructor(
    private authService: AdminAuthService,
    private apiService: AdminApiService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentAdmin = this.authService.currentAdmin();
    this.loadAllData();
  }

  setSection(section: DashboardSection): void {
    this.currentSection = section;
    this.isMobileSidebarOpen = false;

    // Load data for the selected section
    switch (section) {
      case 'overview':
        this.loadStats();
        break;
      case 'reservations':
        this.loadBookings();
        break;
      case 'customers':
        this.loadCustomers();
        break;
      case 'rooms':
        this.loadRooms();
        break;
      case 'payments':
        this.loadPayments();
        break;
    }
  }

  loadAllData(): void {
    this.loadStats();
    if (this.currentSection === 'reservations') this.loadBookings();
    if (this.currentSection === 'customers') this.loadCustomers();
    if (this.currentSection === 'rooms') this.loadRooms();
    if (this.currentSection === 'payments') this.loadPayments();
  }

  // ── Overview Data ───────────────────────────────────────────────────────────
  loadStats(): void {
    this.isLoading = true;
    this.apiService.getStats().subscribe({
      next: (data: AdminStatsResponse) => {
        this.stats = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.handleAuthError(err, 'Failed to load executive statistics.');
      }
    });
  }

  // ── Reservations Data ───────────────────────────────────────────────────────
  loadBookings(): void {
    this.isLoading = true;
    this.apiService.getBookings(this.bookingSearch, this.bookingStatusFilter, this.bookingPage, this.bookingSize).subscribe({
      next: (res: PageResponse<AdminBookingItem>) => {
        this.bookings = res.content || [];
        this.bookingTotalElements = res.totalElements;
        this.bookingTotalPages = res.totalPages;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.handleAuthError(err, 'Failed to load bookings.');
      }
    });
  }

  onBookingSearchChange(): void {
    this.bookingPage = 0;
    this.loadBookings();
  }

  setBookingStatusFilter(status: string): void {
    this.bookingStatusFilter = status;
    this.bookingPage = 0;
    this.loadBookings();
  }

  goToBookingPage(p: number): void {
    if (p >= 0 && p < this.bookingTotalPages) {
      this.bookingPage = p;
      this.loadBookings();
    }
  }

  openBookingStatusModal(booking: AdminBookingItem): void {
    this.selectedBookingForStatus = booking;
    this.newBookingStatus = booking.bookingStatus;
  }

  closeBookingStatusModal(): void {
    this.selectedBookingForStatus = null;
  }

  saveBookingStatus(): void {
    if (!this.selectedBookingForStatus || !this.newBookingStatus) return;

    const bId = this.selectedBookingForStatus.bookingId;
    this.apiService.updateBookingStatus(bId, this.newBookingStatus).subscribe({
      next: (updated: AdminBookingItem) => {
        this.showToast(`Booking #${bId} status updated to ${updated.bookingStatus}`, 'success');
        this.closeBookingStatusModal();
        this.loadBookings();
        this.loadStats();
      },
      error: (err: any) => {
        this.showToast(err.error?.message || 'Failed to update booking status', 'error');
      }
    });
  }

  // ── Customers Data ─────────────────────────────────────────────────────────
  loadCustomers(): void {
    this.isLoading = true;
    this.apiService.getCustomers(this.customerSearch, this.customerPage, this.customerSize).subscribe({
      next: (res: PageResponse<AdminCustomerItem>) => {
        this.customers = res.content || [];
        this.customerTotalElements = res.totalElements;
        this.customerTotalPages = res.totalPages;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.handleAuthError(err, 'Failed to load customers.');
      }
    });
  }

  onCustomerSearchChange(): void {
    this.customerPage = 0;
    this.loadCustomers();
  }

  goToCustomerPage(p: number): void {
    if (p >= 0 && p < this.customerTotalPages) {
      this.customerPage = p;
      this.loadCustomers();
    }
  }

  // ── Rooms Data ─────────────────────────────────────────────────────────────
  loadRooms(): void {
    this.isLoading = true;
    this.apiService.getRooms(this.roomSearch, this.roomTypeFilter, this.roomStatusFilter, this.roomPage, this.roomSize).subscribe({
      next: (res: PageResponse<AdminRoomItem>) => {
        this.rooms = res.content || [];
        this.roomTotalElements = res.totalElements;
        this.roomTotalPages = res.totalPages;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.handleAuthError(err, 'Failed to load rooms.');
      }
    });
  }

  onRoomSearchChange(): void {
    this.roomPage = 0;
    this.loadRooms();
  }

  setRoomTypeFilter(type: string): void {
    this.roomTypeFilter = type;
    this.roomPage = 0;
    this.loadRooms();
  }

  setRoomStatusFilter(status: string): void {
    this.roomStatusFilter = status;
    this.roomPage = 0;
    this.loadRooms();
  }

  goToRoomPage(p: number): void {
    if (p >= 0 && p < this.roomTotalPages) {
      this.roomPage = p;
      this.loadRooms();
    }
  }

  updateRoomStatusQuick(room: AdminRoomItem, newStatus: string): void {
    this.apiService.updateRoomStatus(room.roomId, newStatus).subscribe({
      next: (updated: AdminRoomItem) => {
        room.status = updated.status;
        this.showToast(`Room ${room.roomNumber} set to ${updated.status}`, 'success');
        this.loadStats();
      },
      error: (err: any) => {
        this.showToast(err.error?.message || 'Failed to update room status', 'error');
      }
    });
  }

  openEditPriceModal(room: AdminRoomItem): void {
    this.editingRoom = room;
    this.newRoomPrice = room.pricePerNight;
  }

  closeEditPriceModal(): void {
    this.editingRoom = null;
  }

  saveRoomPrice(): void {
    if (!this.editingRoom || this.newRoomPrice <= 0) return;

    this.apiService.updateRoomPrice(this.editingRoom.roomId, this.newRoomPrice).subscribe({
      next: (updated: AdminRoomItem) => {
        this.editingRoom!.pricePerNight = updated.pricePerNight;
        this.showToast(`Room ${this.editingRoom!.roomNumber} price updated to ₹${updated.pricePerNight}`, 'success');
        this.closeEditPriceModal();
        this.loadRooms();
      },
      error: (err: any) => {
        this.showToast(err.error?.message || 'Failed to update room price', 'error');
      }
    });
  }

  // ── Payments Data ──────────────────────────────────────────────────────────
  loadPayments(): void {
    this.isLoading = true;
    this.apiService.getPayments(this.paymentSearch, this.paymentStatusFilter, this.paymentMethodFilter, this.paymentPage, this.paymentSize).subscribe({
      next: (res: PageResponse<AdminPaymentItem>) => {
        this.payments = res.content || [];
        this.paymentTotalElements = res.totalElements;
        this.paymentTotalPages = res.totalPages;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.handleAuthError(err, 'Failed to load payments.');
      }
    });
  }

  onPaymentSearchChange(): void {
    this.paymentPage = 0;
    this.loadPayments();
  }

  setPaymentStatusFilter(status: string): void {
    this.paymentStatusFilter = status;
    this.paymentPage = 0;
    this.loadPayments();
  }

  setPaymentMethodFilter(method: string): void {
    this.paymentMethodFilter = method;
    this.paymentPage = 0;
    this.loadPayments();
  }

  goToPaymentPage(p: number): void {
    if (p >= 0 && p < this.paymentTotalPages) {
      this.paymentPage = p;
      this.loadPayments();
    }
  }

  // ── Helpers & Auth ─────────────────────────────────────────────────────────
  logout(): void {
    this.authService.logout();
  }

  refreshCurrentData(): void {
    this.setSection(this.currentSection);
    this.showToast('Data refreshed successfully', 'info');
  }

  showToast(text: string, type: 'success' | 'error' | 'info' = 'info'): void {
    if (this.toastTimeout) clearTimeout(this.toastTimeout);
    this.toastMessage = { text, type };
    this.toastTimeout = setTimeout(() => {
      this.toastMessage = null;
      this.cdr.markForCheck();
    }, 4000);
    this.cdr.markForCheck();
  }

  private handleAuthError(err: any, fallback: string): void {
    if (err.status === 401 || err.status === 403) {
      this.showToast('Session expired or unauthorized. Please log in again.', 'error');
      this.authService.logout();
    } else {
      this.showToast(err.error?.message || fallback, 'error');
    }
    this.cdr.markForCheck();
  }

  formatCurrency(val: number | null | undefined): string {
    if (val == null) return '₹0';
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(val);
  }
}
