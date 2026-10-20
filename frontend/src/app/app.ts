import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterModule, Router, NavigationEnd } from '@angular/router';
import { RoomService, ApiRoom, AvailableRoomResponse } from './room.service';
import { BookingService, BookingRequest, BookingResponse } from './booking.service';
import { PaymentService, PaymentRequest, PaymentResponse } from './payment.service';
import { finalize, filter } from 'rxjs/operators';
import { environment } from '../environments/environment';

export interface Room {
  id: string;
  name: string;
  category: string;
  price: number;
  image: string;
  guests: string;
  bed: string;
  size: string;
  amenities: string[];
  description: string;
}

export interface Facility {
  title: string;
  tagline: string;
  image: string;
  description: string;
  highlight: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  // Routing State
  isAdminRoute: boolean = typeof window !== 'undefined' && window.location.pathname.startsWith('/admin');

  // Brand identity
  readonly hotelName = 'ROYAL HOTELS';
  readonly tagline = 'WHERE COMFORT MEETS LUXURY';
  readonly phone = '+91 98765 43210';
  readonly email = 'reservations@royalhotels.com';

  // Navigation View State ('home' | 'booking' | 'payment' | 'confirmation')
  currentView: 'home' | 'booking' | 'payment' | 'confirmation' = 'home';

  // Booking Search & Dates
  checkInDate: string = this.getFutureDateString(1);
  checkOutDate: string = this.getFutureDateString(3);
  guestsCount: string = '2 Guests, 1 Room';
  roomCategory: string = 'All Categories';

  // Interactive UI states
  selectedRoomModal: Room | null = null;
  searchResultMessage: string | null = null;
  isSearchActive: boolean = false;

  // ── API-driven room data (fetched from Java backend) ──────────────────────
  // These are separate from the local rooms[] used by the booking form.
  apiRooms: ApiRoom[] = [];          // all rooms (page-load fetch)
  // Start as TRUE so the spinner renders on the very first frame (before
  // ngOnInit fires). If it started as false, Angular would briefly flash
  // the "No rooms found" empty-state between component creation and the
  // first HTTP response, making it look like rooms only load on user action.
  isLoadingRooms: boolean = true;
  roomsError: string | null = null;  // set if the backend is unreachable

  // ── Date-based availability search results ───────────────────────────────
  // Populated when the user clicks "CHECK AVAILABILITY" with dates selected.
  // Null means no search has been performed yet (show all rooms instead).
  availableRooms: AvailableRoomResponse[] | null = null;
  isLoadingAvailability: boolean = false;
  availabilityError: string | null = null;

  // Key Features / Highlights (matches Reference Mockup 1)
  readonly highlights = [
    {
      icon: 'bed',
      title: 'LUXURY ROOMS',
      desc: 'Spacious and elegant rooms designed for your comfort.'
    },
    {
      icon: 'utensils',
      title: 'FINE DINING',
      desc: 'Indulge in a variety of cuisines crafted by expert chefs.'
    },
    {
      icon: 'spa',
      title: 'WELLNESS & SPA',
      desc: 'Rejuvenate your mind and body with our luxurious spa.'
    },
    {
      icon: 'support',
      title: '24/7 SUPPORT',
      desc: 'We are here to assist you anytime, anywhere.'
    }
  ];

  // Room Catalog (matches Reference Mockup 2 & 3)
  readonly rooms: Room[] = [
    {
      id: 'standard-room',
      name: 'Standard Room',
      category: 'Deluxe Comfort',
      price: 2999,
      image: 'images/standard-room.jpg',
      guests: '2 Guests',
      bed: '1 Queen Bed (or) 2 Single Beds',
      size: '320 sq. ft.',
      amenities: ['Free Wi-Fi', 'Air Conditioning', 'Flat TV', 'Work Desk', 'Mini Bar', '24h Room Service'],
      description: 'Tastefully furnished with contemporary aesthetics, soothing ambiance, and premium bedding for a restful stay.'
    },
    {
      id: 'deluxe-room',
      name: 'Deluxe Room',
      category: 'Premium Luxury',
      price: 4499,
      image: 'images/deluxe-room.jpg',
      guests: '2 Guests',
      bed: '1 King Bed',
      size: '450 sq. ft.',
      amenities: ['Free Wi-Fi', 'Air Conditioning', '55" 4K Smart TV', 'Work Desk', 'Mini Bar', 'Private Balcony', 'Bathtub'],
      description: 'Experience elegance and comfort with panoramic scenic views, stylish interiors, and a peaceful ambiance for a truly memorable stay.'
    },
    {
      id: 'suite',
      name: 'Royal Suite',
      category: 'Palace Experience',
      price: 7499,
      image: 'images/royal-suite.jpg',
      guests: '4 Guests',
      bed: '1 King Bed + Lounge',
      size: '720 sq. ft.',
      amenities: ['Free Wi-Fi', 'Air Conditioning', '65" OLED TV', 'Separate Living Room', 'Mini Bar', 'Butler Service', 'Jacuzzi'],
      description: 'The pinnacle of luxury featuring an expansive royal lounge, dedicated butler service, bespoke chandelier lighting, and majestic city vistas.'
    }
  ];

  // Active Selected Room for the Booking Journey
  selectedBookingRoom: Room = this.rooms[1]; // defaults to Deluxe Room
  selectedRoomId: number = 4;                // default to room_id 4 (Room 201 Deluxe)
  selectedRoomNumber: string = '201';

  // Booking submission states
  isSubmittingBooking: boolean = false;
  bookingError: string | null = null;
  confirmedBooking: BookingResponse | null = null;

  // Payment states & demo credentials
  selectedPaymentMethod: 'UPI' | 'Card' | 'Cash' = 'UPI';
  demoUpiId: string = 'guest@okhdfcbank';
  demoCardNumber: string = '4532 •••• •••• 4242';
  demoCardExpiry: string = '12/28';
  demoCardHolder: string = '';
  isSubmittingPayment: boolean = false;
  paymentError: string | null = null;
  confirmedPayment: PaymentResponse | null = null;

  // Inject RoomService, BookingService, PaymentService & Router
  constructor(
    private roomService: RoomService,
    private bookingService: BookingService,
    private paymentService: PaymentService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd)
    ).subscribe((e: NavigationEnd) => {
      this.isAdminRoute = e.urlAfterRedirects.startsWith('/admin');
      this.cdr.markForCheck();
    });
  }

  /**
   * ngOnInit fires automatically once, immediately after Angular creates this
   * component. It calls loadRooms() so room data fetches on page open with
   * no user interaction required.
   */
  ngOnInit(): void {
    if (!this.isAdminRoute) {
      this.loadRooms();
    }
  }

  /**
   * loadRooms() is the single place that fires the HTTP GET /api/rooms request.
   *
   * Called by ngOnInit on page load automatically.
   * Called by onCheckAvailability only as a retry (when apiRooms is still empty).
   *
   * finalize()  — resets isLoadingRooms to false whether the request succeeds,
   *               fails, or completes without emitting (edge case).
   * markForCheck() — explicitly tells Angular "this component's state changed,
   *               please re-render it". This is the critical fix for Angular 22's
   *               zoneless default: without zone.js, async callbacks don't
   *               automatically trigger change detection. markForCheck() forces it.
   */
  private loadRooms(): void {
    this.isLoadingRooms = true;
    this.roomsError = null;
    this.cdr.markForCheck(); // mark dirty before the request so the spinner renders

    this.roomService.getAllRooms()
      .pipe(
        finalize(() => {
          // Always runs: after next+complete OR after error.
          // Guarantees isLoadingRooms resets even if the Observable completes
          // without emitting a value (defensive guard).
          this.isLoadingRooms = false;
          this.cdr.markForCheck(); // force view update on completion
        })
      )
      .subscribe({
        next: (data: ApiRoom[]) => {
          console.log(`[RoomService] Loaded ${data.length} rooms from API.`, data);
          this.apiRooms = data;
          this.syncSelectedRoomWithApi();
          // markForCheck() here is belt-and-suspenders in case finalize fires
          // in a different microtask from next.
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('[RoomService] Failed to load rooms:', err);
          this.roomsError =
            'Unable to load rooms from the backend. ' +
            `Ensure the backend server is reachable at ${environment.apiUrl}.`;
          this.cdr.markForCheck();
        }
      });
  }

  // Customer Details Form Model (Reference Mockup 4)
  guestName: string = '';
  guestPhone: string = '';
  guestEmail: string = '';
  companyName: string = '';
  purposeOfStay: string = 'Leisure & Holiday';
  specialRequests: string = '';

  // Form Validation & Confirmation
  validationErrors: { [key: string]: string } = {};
  confirmedBookingId: string = '';
  confirmationDate: string = '';

  // Facilities & Heritage Experiences (Taj Rambagh Palace inspiration)
  readonly facilities: Facility[] = [
    {
      title: 'The Royal Pavilion Dining',
      tagline: 'Gourmet Gastronomy & Royal Recipes',
      image: 'images/dining.jpg',
      description: 'Experience exquisite culinary delights prepared by master chefs under glistening crystal chandeliers, serving authentic heritage recipes alongside international haute cuisine.',
      highlight: 'Breakfast Buffet & Candlelit Dinners'
    },
    {
      title: 'Heritage Wellness & Ayurveda Spa',
      tagline: 'Holistic Mind & Body Rejuvenation',
      image: 'images/spa.jpg',
      description: 'Immerse yourself in serene tranquility with therapeutic Ayurvedic treatments, aromatic floral baths, and bespoke holistic wellness therapies designed to restore balance.',
      highlight: 'Ancient Therapy & Steam Chambers'
    }
  ];

  // Helper to generate default future dates (YYYY-MM-DD)
  private getFutureDateString(daysAhead: number): string {
    const d = new Date();
    d.setDate(d.getDate() + daysAhead);
    return d.toISOString().split('T')[0];
  }

  // --- Dynamic Pricing Calculations ---
  get calculatedNights(): number {
    if (!this.checkInDate || !this.checkOutDate) return 1;
    const start = new Date(this.checkInDate).getTime();
    const end = new Date(this.checkOutDate).getTime();
    const diffDays = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
    return diffDays > 0 ? diffDays : 0;
  }

  get isDateRangeValid(): boolean {
    if (!this.checkInDate || !this.checkOutDate) return false;
    const start = new Date(this.checkInDate).getTime();
    const end = new Date(this.checkOutDate).getTime();
    return end > start;
  }

  get roomTotalCharge(): number {
    const nights = this.calculatedNights > 0 ? this.calculatedNights : 1;
    return this.selectedBookingRoom.price * nights;
  }

  get estimatedTaxes(): number {
    // 12% GST luxury hospitality rate
    return Math.round(this.roomTotalCharge * 0.12);
  }

  get estimatedGrandTotal(): number {
    return this.roomTotalCharge + this.estimatedTaxes;
  }

  // --- Booking Flow Navigation ---
  startBooking(roomInput?: Room | ApiRoom | string): void {
    this.bookingError = null;
    this.validationErrors = {};

    if (roomInput) {
      if (typeof roomInput === 'string') {
        const found = this.rooms.find(r => r.name.toLowerCase() === roomInput.toLowerCase() || r.id === roomInput);
        if (found) {
          this.selectedBookingRoom = found;
          this.syncSelectedRoomWithApi();
        }
      } else if ('roomNumber' in roomInput) {
        // It's an ApiRoom from the database
        const apiRoom = roomInput as ApiRoom;
        this.selectedRoomId = apiRoom.roomId;
        this.selectedRoomNumber = apiRoom.roomNumber;
        const matchingLocal = this.rooms.find(r =>
          r.name.toLowerCase().includes(apiRoom.roomType.toLowerCase())
        );
        if (matchingLocal) {
          this.selectedBookingRoom = matchingLocal;
        }
      } else {
        // It's a local Room definition
        this.selectedBookingRoom = roomInput as Room;
        this.syncSelectedRoomWithApi();
      }
    } else {
      this.syncSelectedRoomWithApi();
    }

    // Close preview modal if opened
    this.selectedRoomModal = null;
    this.currentView = 'booking';
    this.scrollToTop();
  }

  selectBookingRoom(room: Room): void {
    this.selectedBookingRoom = room;
    this.syncSelectedRoomWithApi();
  }

  selectBookingRoomById(roomId: string): void {
    const found = this.rooms.find(r => r.id === roomId);
    if (found) {
      this.selectedBookingRoom = found;
      this.syncSelectedRoomWithApi();
    }
  }

  private syncSelectedRoomWithApi(): void {
    const type = this.selectedBookingRoom.name.includes('Suite') ? 'Suite' :
                 this.selectedBookingRoom.name.includes('Deluxe') ? 'Deluxe' : 'Standard';

    // Prefer an AVAILABLE room of the chosen category from live MySQL data
    const matched = this.apiRooms.find(r => r.roomType === type && r.status === 'AVAILABLE') ||
                    this.apiRooms.find(r => r.roomType === type);

    if (matched) {
      this.selectedRoomId = matched.roomId;
      this.selectedRoomNumber = matched.roomNumber;
    } else {
      // Fallback IDs corresponding to seed data: Standard=1, Deluxe=4, Suite=7
      this.selectedRoomId = type === 'Suite' ? 7 : type === 'Deluxe' ? 4 : 1;
      this.selectedRoomNumber = type === 'Suite' ? '301' : type === 'Deluxe' ? '201' : '101';
    }
  }

  navigateToHome(): void {
    this.currentView = 'home';
    this.scrollToTop();
  }

  // --- Form Validation & Submission ---
  submitReservationRequest(): void {
    this.validationErrors = {};
    this.bookingError = null;

    // 1. Validate Check-in & Check-out Dates
    if (!this.checkInDate) {
      this.validationErrors['checkIn'] = 'Please select a check-in date.';
    }
    if (!this.checkOutDate) {
      this.validationErrors['checkOut'] = 'Please select a check-out date.';
    } else if (!this.isDateRangeValid) {
      this.validationErrors['checkOut'] = 'Check-out date must be strictly after the check-in date.';
    } else {
      // Validate check-in date is not in the past
      const todayStr = new Date().toISOString().split('T')[0];
      if (this.checkInDate < todayStr) {
        this.validationErrors['checkIn'] = 'Check-in date cannot be in the past.';
      }
    }

    // 2. Validate Guest Name (minimum 3 characters)
    if (!this.guestName || this.guestName.trim().length < 3) {
      this.validationErrors['guestName'] = 'Please enter your full name (minimum 3 characters).';
    }

    // 3. Validate Phone Number (must be valid 10 digits)
    const cleanPhone = this.guestPhone.replace(/\D/g, '');
    if (!this.guestPhone || cleanPhone.length < 10) {
      this.validationErrors['guestPhone'] = 'Please enter a valid 10-digit phone number.';
    }

    // 4. Validate Email Address
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!this.guestEmail || !emailRegex.test(this.guestEmail.trim())) {
      this.validationErrors['guestEmail'] = 'Please provide a valid email address.';
    }

    // If there are validation errors, stop immediately
    if (Object.keys(this.validationErrors).length > 0) {
      this.cdr.markForCheck();
      return;
    }

    // Prevent duplicate submissions while request is in progress
    if (this.isSubmittingBooking) {
      return;
    }

    // Parse guest count as a positive integer
    const guestMatches = this.guestsCount.match(/\d+/);
    const numGuests = guestMatches ? parseInt(guestMatches[0], 10) : 1;

    // Ensure selected room ID is synchronized
    if (!this.selectedRoomId) {
      this.syncSelectedRoomWithApi();
    }

    // Build the request payload matching BookingRequest.java exactly
    const bookingPayload: BookingRequest = {
      roomId: this.selectedRoomId,
      customerName: this.guestName.trim(),
      customerPhone: cleanPhone,
      customerEmail: this.guestEmail.trim(),
      customerAddress: this.companyName ? `Company: ${this.companyName.trim()}` : undefined,
      customerIdProof: this.specialRequests ? `Special Requests: ${this.specialRequests.trim()}` : undefined,
      checkIn: this.checkInDate,
      checkOut: this.checkOutDate,
      numberOfGuests: numGuests
    };

    this.isSubmittingBooking = true;
    this.bookingError = null;
    this.cdr.markForCheck();

    this.bookingService.createBooking(bookingPayload)
      .pipe(
        finalize(() => {
          this.isSubmittingBooking = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (response: BookingResponse) => {
          console.log('[BookingService] Confirmed booking response:', response);
          this.confirmedBooking = response;
          this.confirmedBookingId = `RH-BK-${response.bookingId}`;
          this.demoCardHolder = response.customerName || this.guestName;
          this.confirmationDate = new Date().toLocaleDateString('en-GB', {
            day: 'numeric',
            month: 'short',
            year: 'numeric'
          });
          // Proceed to Payment / Checkout page for this actual booking
          this.currentView = 'payment';
          this.paymentError = null;
          this.confirmedPayment = null;
          this.scrollToTop();
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('[BookingService] Booking creation failed:', err);
          if (err.status === 409) {
            this.bookingError = err.error?.message ||
              'This room is already reserved for the selected dates. Please choose different dates or another room.';
          } else if (err.status === 400) {
            this.bookingError = err.error?.message ||
              'Invalid reservation details. Please check your dates and guest information.';
          } else if (err.status === 0) {
            this.bookingError =
              `Unable to connect to the backend server. Please verify the application is reachable at ${environment.apiUrl}.`;
          } else {
            this.bookingError = err.error?.message ||
              'An unexpected error occurred while processing your booking. Please try again.';
          }
          this.cdr.markForCheck();
        }
      });
  }

  // --- Payment & Checkout Handlers ---
  selectPaymentMethod(method: 'UPI' | 'Card' | 'Cash'): void {
    this.selectedPaymentMethod = method;
    this.paymentError = null;
    this.cdr.markForCheck();
  }

  submitPayment(simulateSuccess: boolean): void {
    if (!this.confirmedBooking) {
      this.paymentError = 'No confirmed booking found to pay for. Please submit a reservation first.';
      this.cdr.markForCheck();
      return;
    }

    if (this.isSubmittingPayment) {
      return;
    }

    this.isSubmittingPayment = true;
    this.paymentError = null;
    this.cdr.markForCheck();

    const paymentRequest: PaymentRequest = {
      bookingId: this.confirmedBooking.bookingId,
      paymentMethod: this.selectedPaymentMethod,
      simulateSuccess: simulateSuccess
    };

    this.paymentService.processPayment(paymentRequest)
      .pipe(
        finalize(() => {
          this.isSubmittingPayment = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (response: PaymentResponse) => {
          console.log('[PaymentService] Payment processed:', response);
          if (response.success) {
            this.confirmedPayment = response;
            this.currentView = 'confirmation';
            this.scrollToTop();
          } else {
            // Simulated decline/failure
            this.paymentError = response.message ||
              'Demo payment was declined in simulation. No charges were made. You may retry or choose another payment method.';
          }
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('[PaymentService] Payment request failed:', err);
          if (err.status === 409) {
            this.paymentError = err.error?.message ||
              'This booking has already been paid in full. Duplicate payment prevented.';
          } else if (err.status === 400) {
            this.paymentError = err.error?.message ||
              'Invalid payment request details.';
          } else if (err.status === 0) {
            this.paymentError =
              `Unable to reach payment server. Please verify the backend is reachable at ${environment.apiUrl}.`;
          } else {
            this.paymentError = err.error?.message ||
              'Payment processing error occurred. Please try again.';
          }
          this.cdr.markForCheck();
        }
      });
  }

  printReceipt(): void {
    window.print();
  }

  // --- Search Bar Handler on Home ---
  onCheckAvailability(): void {
    // Basic date sanity check before firing the API
    if (!this.checkInDate || !this.checkOutDate) {
      this.searchResultMessage = 'Please select both a check-in and check-out date.';
      this.isSearchActive = true;
      this.cdr.markForCheck();
      return;
    }
    if (!this.isDateRangeValid) {
      this.searchResultMessage = 'Check-out date must be after check-in date.';
      this.isSearchActive = true;
      this.cdr.markForCheck();
      return;
    }

    // Parse numeric guest count from dropdown string (e.g. "2 Guests, 1 Room" → 2)
    const guestMatches = this.guestsCount.match(/\d+/);
    const numGuests = guestMatches ? parseInt(guestMatches[0], 10) : 1;

    this.isSearchActive = true;
    this.isLoadingAvailability = true;
    this.availabilityError = null;
    this.availableRooms = null;
    this.searchResultMessage =
      `Checking availability for ${this.guestsCount} ` +
      `from ${this.checkInDate} → ${this.checkOutDate}…`;
    this.cdr.markForCheck();

    // Scroll to rooms section immediately so the user sees the loading state
    this.scrollToSection('rooms-section');

    // Call the dynamic date-based availability endpoint:
    // GET /api/rooms/available?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD&guests=N
    this.roomService.checkAvailability(this.checkInDate, this.checkOutDate, numGuests)
      .pipe(
        finalize(() => {
          this.isLoadingAvailability = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (rooms: AvailableRoomResponse[]) => {
          console.log(`[Availability] ${rooms.length} rooms available for ${this.checkInDate} → ${this.checkOutDate}`, rooms);
          this.availableRooms = rooms;
          if (rooms.length === 0) {
            this.searchResultMessage =
              `No rooms available for ${this.checkInDate} → ${this.checkOutDate}. ` +
              `Please try different dates.`;
          } else {
            this.searchResultMessage =
              `${rooms.length} room${rooms.length !== 1 ? 's' : ''} available ` +
              `for ${this.checkInDate} → ${this.checkOutDate}. Select a room below.`;
          }
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('[Availability] API error:', err);
          this.availabilityError =
            err.error?.message ||
            `Could not fetch availability. Ensure the server is reachable at ${environment.apiUrl}.`;
          this.searchResultMessage = null;
          this.cdr.markForCheck();
        }
      });
  }

  /** Reset availability search (e.g. when the user manually clears dates) */
  clearAvailabilitySearch(): void {
    this.availableRooms = null;
    this.availabilityError = null;
    this.isSearchActive = false;
    this.searchResultMessage = null;
    this.cdr.markForCheck();
  }

  // --- Modal Helpers ---
  openRoomModal(room: Room): void {
    this.selectedRoomModal = room;
  }

  closeRoomModal(): void {
    this.selectedRoomModal = null;
  }

  // --- Scroll Helpers ---
  scrollToSection(sectionId: string): void {
    if (this.currentView !== 'home') {
      this.currentView = 'home';
      setTimeout(() => {
        const elem = document.getElementById(sectionId);
        if (elem) elem.scrollIntoView({ behavior: 'smooth' });
      }, 100);
      return;
    }
    const elem = document.getElementById(sectionId);
    if (elem) {
      elem.scrollIntoView({ behavior: 'smooth' });
    }
  }

  private scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
