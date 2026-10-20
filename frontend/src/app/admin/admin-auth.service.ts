import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

export interface AdminUser {
  username: string;
  fullName: string;
  role: string;
}

export interface AdminLoginResponse {
  success: boolean;
  token?: string;
  tokenType?: string;
  username?: string;
  fullName?: string;
  role?: string;
  expiresInMs?: number;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminAuthService {
  private readonly baseUrl = `${environment.apiUrl}/admin/auth`;
  private readonly tokenKey = 'royal_hotels_admin_token';
  private readonly userKey = 'royal_hotels_admin_user';

  currentAdmin = signal<AdminUser | null>(this.getStoredUser());

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { username: string; password: string }): Observable<AdminLoginResponse> {
    return this.http.post<AdminLoginResponse>(`${this.baseUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.success && response.token) {
          this.setSession(response.token, {
            username: response.username || credentials.username,
            fullName: response.fullName || 'Administrator',
            role: response.role || 'ROLE_ADMIN'
          });
        }
      })
    );
  }

  logout(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userKey);
    }
    this.currentAdmin.set(null);
    this.router.navigate(['/admin/login']);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      // Decode JWT payload to check expiration
      const parts = token.split('.');
      if (parts.length !== 3) return false;
      const payload = JSON.parse(atob(parts[1]));
      if (payload.exp && Date.now() >= payload.exp * 1000) {
        this.logout();
        return false;
      }
      return true;
    } catch {
      return false;
    }
  }

  getToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  private setSession(token: string, user: AdminUser): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.tokenKey, token);
      localStorage.setItem(this.userKey, JSON.stringify(user));
    }
    this.currentAdmin.set(user);
  }

  private getStoredUser(): AdminUser | null {
    if (typeof window !== 'undefined') {
      const stored = localStorage.getItem(this.userKey);
      if (stored) {
        try {
          return JSON.parse(stored);
        } catch {
          return null;
        }
      }
    }
    return null;
  }
}
