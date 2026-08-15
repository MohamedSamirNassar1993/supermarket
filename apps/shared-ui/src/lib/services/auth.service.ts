import { Injectable, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { API_BASE_URL } from '../tokens/api-config.token';
import { ApiResponse } from '../models/api-response.model';
import { AuthResponse, LoginRequest, RefreshTokenRequest, UserSummary } from '../models/auth.model';
import { BranchContextService } from './branch-context.service';

const TOKEN_KEY = 'sm_access_token';
const REFRESH_KEY = 'sm_refresh_token';
const USER_KEY = 'sm_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly branchContext = inject(BranchContextService);
  private readonly baseUrl = inject(API_BASE_URL);

  private readonly userSignal = signal<UserSummary | null>(this.loadUser());
  readonly user = this.userSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.getAccessToken());
  readonly permissions = computed(() => this.userSignal()?.permissions ?? []);

  private readonly authState$ = new BehaviorSubject<boolean>(!!this.getAccessToken());
  readonly authState = this.authState$.asObservable();

  login(request: LoginRequest, organizationId?: string): Observable<AuthResponse> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/auth/login`, request)
      .pipe(
        map((res) => res.data),
        tap((auth) => {
          if (organizationId) {
            this.branchContext.setOrganizationId(organizationId);
          }
          this.persistSession(auth);
        }),
        catchError((err) => throwError(() => err))
      );
  }

  refresh(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem(REFRESH_KEY);
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token'));
    }
    const request: RefreshTokenRequest = { refreshToken };
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/auth/refresh`, request)
      .pipe(
        map((res) => res.data),
        tap((auth) => this.persistSession(auth))
      );
  }

  logout(): void {
    const refreshToken = localStorage.getItem(REFRESH_KEY);
    this.http
      .post(`${this.baseUrl}/auth/logout`, refreshToken ? { refreshToken } : {})
      .subscribe({ complete: () => this.clearSession() });
    this.clearSession();
  }

  getAccessToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  hasPermission(permission: string): boolean {
    return this.permissions().includes(permission);
  }

  hasAnyPermission(...permissions: string[]): boolean {
    const userPerms = this.permissions();
    return permissions.some((p) => userPerms.includes(p));
  }

  private persistSession(auth: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, auth.accessToken);
    localStorage.setItem(REFRESH_KEY, auth.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(auth.user));
    this.userSignal.set(auth.user);
    this.authState$.next(true);
    if (auth.user.organizationId) {
      this.branchContext.setOrganizationId(auth.user.organizationId);
    }
  }

  private clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    this.userSignal.set(null);
    this.authState$.next(false);
    this.router.navigate(['/login']);
  }

  private loadUser(): UserSummary | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserSummary;
    } catch {
      return null;
    }
  }
}
