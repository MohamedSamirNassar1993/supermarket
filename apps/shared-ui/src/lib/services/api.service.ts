import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { API_BASE_URL } from '../tokens/api-config.token';
import { ApiResponse, PageResponse } from '../models/api-response.model';

export interface PageParams {
  page?: number;
  size?: number;
  sort?: string;
  [key: string]: string | number | boolean | undefined;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);

  get<T>(path: string, params?: PageParams): Observable<T> {
    return this.http
      .get<ApiResponse<T>>(`${this.baseUrl}${path}`, { params: this.buildParams(params) })
      .pipe(map((res) => this.unwrap(res)));
  }

  getPage<T>(path: string, params?: PageParams): Observable<PageResponse<T>> {
    return this.get<PageResponse<T>>(path, params);
  }

  post<T>(path: string, body: unknown, params?: PageParams): Observable<T> {
    return this.http
      .post<ApiResponse<T>>(`${this.baseUrl}${path}`, body, { params: this.buildParams(params) })
      .pipe(map((res) => this.unwrap(res)));
  }

  put<T>(path: string, body: unknown, params?: PageParams): Observable<T> {
    return this.http
      .put<ApiResponse<T>>(`${this.baseUrl}${path}`, body, { params: this.buildParams(params) })
      .pipe(map((res) => this.unwrap(res)));
  }

  delete<T>(path: string, params?: PageParams): Observable<T> {
    return this.http
      .delete<ApiResponse<T>>(`${this.baseUrl}${path}`, { params: this.buildParams(params) })
      .pipe(map((res) => this.unwrap(res)));
  }

  private buildParams(params?: PageParams): HttpParams | undefined {
    if (!params) return undefined;
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        httpParams = httpParams.set(key, String(value));
      }
    });
    return httpParams;
  }

  private unwrap<T>(response: ApiResponse<T>): T {
    if (!response.success && response.data === undefined) {
      throw new Error(response.message || 'Request failed');
    }
    return response.data as T;
  }
}
