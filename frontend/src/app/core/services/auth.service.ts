import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { AuthResponse, ApiResponse, User } from '../models/vms.models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.getUserFromLocalStorage());
  
  // Using signals for reactive components (v16 feature)
  currentUser = signal<AuthResponse | null>(this.getUserFromLocalStorage());

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        if (response.success) {
          this.setSession(response.data);
        }
      })
    );
  }

  register(userData: any): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.API_URL}/register`, userData).pipe(
      tap(response => {
        if (response.success) {
          this.setSession(response.data);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('vms_user');
    this.currentUserSubject.next(null);
    this.currentUser.set(null);
  }

  private setSession(authResponse: AuthResponse): void {
    localStorage.setItem('vms_user', JSON.stringify(authResponse));
    this.currentUserSubject.next(authResponse);
    this.currentUser.set(authResponse);
  }

  private getUserFromLocalStorage(): AuthResponse | null {
    const userJson = localStorage.getItem('vms_user');
    return userJson ? JSON.parse(userJson) : null;
  }

  get token(): string | null {
    return this.currentUser()?.token || null;
  }

  isLoggedIn(): boolean {
    return !!this.token;
  }

  hasRole(role: string): boolean {
    return this.currentUser()?.role === role;
  }
}
