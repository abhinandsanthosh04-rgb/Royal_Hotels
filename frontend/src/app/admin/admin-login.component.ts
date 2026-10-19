import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AdminAuthService, AdminLoginResponse } from './admin-auth.service';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="admin-login-wrapper">
      <div class="luxury-bg-overlay"></div>

      <div class="login-container">
        <!-- Brand Header -->
        <div class="brand-crest">
          <img src="logo.png" alt="Royal Hotels Emblem" class="crest-logo" />
          <div class="gold-seal">★ ★ ★ ★ ★</div>
          <h1 class="hotel-title">ROYAL HOTELS</h1>
          <p class="hotel-subtitle">EXECUTIVE MANAGEMENT PORTAL</p>
          <div class="gold-divider">
            <span class="diamond">♦</span>
          </div>
        </div>

        <!-- Login Card -->
        <div class="login-card">
          <h2 class="card-title">Sign In to Dashboard</h2>
          <p class="card-desc">Enter authorized administrative credentials to access operations</p>

          <!-- Error Alert -->
          @if (errorMessage) {
            <div class="alert-error" role="alert">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="alert-icon">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
              </svg>
              <span>{{ errorMessage }}</span>
            </div>
          }

          <form (ngSubmit)="onSubmit()" class="login-form">
            <!-- Username Input -->
            <div class="form-group">
              <label for="username">Administrator Username</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <input
                  type="text"
                  id="username"
                  name="username"
                  [(ngModel)]="username"
                  required
                  placeholder="e.g. admin"
                  autocomplete="username"
                  class="luxury-input"
                  [disabled]="isLoading"
                />
              </div>
            </div>

            <!-- Password Input -->
            <div class="form-group">
              <label for="password">Security Password</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                <input
                  [type]="showPassword ? 'text' : 'password'"
                  id="password"
                  name="password"
                  [(ngModel)]="password"
                  required
                  placeholder="Enter administrator password"
                  autocomplete="current-password"
                  class="luxury-input"
                  [disabled]="isLoading"
                />
                <button
                  type="button"
                  (click)="showPassword = !showPassword"
                  class="btn-eye"
                  aria-label="Toggle password visibility"
                >
                  @if (showPassword) {
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="eye-icon">
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                      <line x1="1" y1="1" x2="23" y2="23"></line>
                    </svg>
                  } @else {
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="eye-icon">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                      <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                  }
                </button>
              </div>
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              class="btn-gold-submit"
              [disabled]="isLoading || !username || !password"
            >
              @if (isLoading) {
                <span class="spinner"></span>
                <span>AUTHENTICATING...</span>
              } @else {
                <span>ENTER PORTAL</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="btn-arrow">
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                  <polyline points="12 5 19 12 12 19"></polyline>
                </svg>
              }
            </button>
          </form>

          <!-- Back to Public Site -->
          <div class="card-footer">
            <a routerLink="/" class="back-link">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="back-icon">
                <line x1="19" y1="12" x2="5" y2="12"></line>
                <polyline points="12 19 5 12 12 5"></polyline>
              </svg>
              <span>Return to Guest Website</span>
            </a>
          </div>
        </div>

        <div class="system-notice">
          <p>Protected System — Authorized Royal Hotels Personnel Only</p>
          <p class="timestamp">Session ID: 2026-RH-SEC • Spring Boot &amp; JWT Encrypted</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .admin-login-wrapper {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: radial-gradient(ellipse at center, #111a2e 0%, #080d18 70%, #04070d 100%);
      position: relative;
      padding: 2rem 1.5rem;
      font-family: var(--font-body, 'Montserrat', sans-serif);
      color: #f8fafc;
      overflow: hidden;
    }

    .luxury-bg-overlay {
      position: absolute;
      inset: 0;
      background: 
        radial-gradient(circle at 20% 20%, rgba(197, 168, 128, 0.08) 0%, transparent 40%),
        radial-gradient(circle at 80% 80%, rgba(212, 175, 55, 0.06) 0%, transparent 45%);
      pointer-events: none;
    }

    .login-container {
      position: relative;
      z-index: 2;
      width: 100%;
      max-width: 460px;
      display: flex;
      flex-direction: column;
      align-items: center;
    }

    .brand-crest {
      text-align: center;
      margin-bottom: 1.75rem;
    }

    .crest-logo {
      width: 72px;
      height: 72px;
      border-radius: 50%;
      border: 2px solid #c5a880;
      box-shadow: 0 0 25px rgba(197, 168, 128, 0.35);
      margin-bottom: 0.75rem;
      object-fit: cover;
    }

    .gold-seal {
      color: #d4af37;
      letter-spacing: 4px;
      font-size: 0.75rem;
      margin-bottom: 0.35rem;
    }

    .hotel-title {
      font-family: var(--font-serif, 'Playfair Display', Georgia, serif);
      font-size: 2rem;
      font-weight: 700;
      letter-spacing: 3px;
      color: #f8fafc;
      margin: 0;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
    }

    .hotel-subtitle {
      font-size: 0.75rem;
      font-weight: 600;
      letter-spacing: 3px;
      color: #c5a880;
      margin-top: 0.25rem;
    }

    .gold-divider {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-top: 0.75rem;
      position: relative;
    }

    .gold-divider::before,
    .gold-divider::after {
      content: '';
      width: 60px;
      height: 1px;
      background: linear-gradient(90deg, transparent, #c5a880, transparent);
    }

    .diamond {
      color: #c5a880;
      font-size: 0.65rem;
      margin: 0 0.5rem;
    }

    .login-card {
      width: 100%;
      background: rgba(17, 26, 46, 0.85);
      backdrop-filter: blur(16px);
      -webkit-backdrop-filter: blur(16px);
      border: 1px solid rgba(197, 168, 128, 0.3);
      border-radius: 14px;
      padding: 2.25rem 2rem;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.7), 0 0 40px rgba(197, 168, 128, 0.1);
    }

    .card-title {
      font-family: var(--font-serif, 'Playfair Display', Georgia, serif);
      font-size: 1.45rem;
      font-weight: 600;
      color: #ffffff;
      margin-bottom: 0.35rem;
      text-align: center;
    }

    .card-desc {
      font-size: 0.85rem;
      color: #94a3b8;
      text-align: center;
      margin-bottom: 1.5rem;
    }

    .alert-error {
      background: rgba(239, 68, 68, 0.15);
      border: 1px solid rgba(239, 68, 68, 0.4);
      color: #fca5a5;
      padding: 0.75rem 1rem;
      border-radius: 8px;
      font-size: 0.85rem;
      display: flex;
      align-items: center;
      gap: 0.6rem;
      margin-bottom: 1.25rem;
      animation: fadeIn 0.25s ease;
    }

    .alert-icon {
      width: 18px;
      height: 18px;
      flex-shrink: 0;
      color: #ef4444;
    }

    .form-group {
      margin-bottom: 1.25rem;
    }

    .form-group label {
      display: block;
      font-size: 0.8rem;
      font-weight: 500;
      letter-spacing: 0.5px;
      color: #cbd5e1;
      margin-bottom: 0.4rem;
    }

    .input-wrapper {
      position: relative;
      display: flex;
      align-items: center;
    }

    .input-icon {
      position: absolute;
      left: 1rem;
      width: 18px;
      height: 18px;
      color: #c5a880;
      pointer-events: none;
    }

    .luxury-input {
      width: 100%;
      background: rgba(8, 13, 24, 0.7);
      border: 1px solid rgba(197, 168, 128, 0.25);
      border-radius: 8px;
      padding: 0.85rem 2.8rem 0.85rem 2.8rem;
      color: #ffffff;
      font-size: 0.92rem;
      font-family: inherit;
      transition: all 0.2s ease;
    }

    .luxury-input:focus {
      outline: none;
      border-color: #d4af37;
      box-shadow: 0 0 15px rgba(212, 175, 55, 0.25);
      background: rgba(8, 13, 24, 0.9);
    }

    .btn-eye {
      position: absolute;
      right: 0.85rem;
      background: transparent;
      border: none;
      color: #94a3b8;
      cursor: pointer;
      padding: 0.25rem;
      display: flex;
      align-items: center;
      transition: color 0.2s;
    }

    .btn-eye:hover {
      color: #d4af37;
    }

    .eye-icon {
      width: 18px;
      height: 18px;
    }

    .btn-gold-submit {
      width: 100%;
      background: linear-gradient(135deg, #d4af37 0%, #c5a880 50%, #aa8245 100%);
      color: #090e18;
      border: none;
      border-radius: 8px;
      padding: 0.95rem;
      font-size: 0.88rem;
      font-weight: 700;
      letter-spacing: 1.5px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.6rem;
      margin-top: 1.5rem;
      transition: all 0.25s ease;
      box-shadow: 0 4px 15px rgba(197, 168, 128, 0.3);
    }

    .btn-gold-submit:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(197, 168, 128, 0.5);
      filter: brightness(1.08);
    }

    .btn-gold-submit:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }

    .btn-arrow {
      width: 18px;
      height: 18px;
      transition: transform 0.2s;
    }

    .btn-gold-submit:hover .btn-arrow {
      transform: translateX(4px);
    }

    .spinner {
      width: 16px;
      height: 16px;
      border: 2px solid rgba(9, 14, 24, 0.3);
      border-top-color: #090e18;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }

    .card-footer {
      margin-top: 1.5rem;
      text-align: center;
      border-top: 1px solid rgba(255, 255, 255, 0.08);
      padding-top: 1.25rem;
    }

    .back-link {
      display: inline-flex;
      align-items: center;
      gap: 0.4rem;
      color: #c5a880;
      text-decoration: none;
      font-size: 0.82rem;
      font-weight: 500;
      transition: color 0.2s;
    }

    .back-link:hover {
      color: #ffffff;
    }

    .back-icon {
      width: 15px;
      height: 15px;
    }

    .system-notice {
      margin-top: 1.5rem;
      text-align: center;
      font-size: 0.72rem;
      color: #64748b;
      line-height: 1.5;
    }

    .system-notice .timestamp {
      color: #475569;
      font-size: 0.68rem;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(-4px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class AdminLoginComponent {
  username = '';
  password = '';
  showPassword = false;
  isLoading = false;
  errorMessage: string | null = null;

  constructor(private authService: AdminAuthService, private router: Router) {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/admin/dashboard']);
    }
  }

  onSubmit(): void {
    if (!this.username || !this.password) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.authService.login({
      username: this.username.trim(),
      password: this.password
    }).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.errorMessage = response.message || 'Authentication failed. Please verify credentials.';
        }
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 401) {
          this.errorMessage = 'Invalid username or password.';
        } else if (err.status === 0) {
          this.errorMessage = 'Backend server unreachable. Ensure Spring Boot is running on port 8080.';
        } else {
          this.errorMessage = err.error?.message || 'Authentication failed. Please try again.';
        }
      }
    });
  }
}
