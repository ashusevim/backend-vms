import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/vms.models';
import { environment } from '../../../environments/environment';

export interface VisitLog {
  id: number;
  checkInTime: string;
  checkOutTime?: string;
  visitorName: string;
  associateName: string;
  purpose: string;
  badgeNumber?: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class VisitLogService {
  private readonly API_URL = `${environment.apiUrl}/visit-logs`;

  constructor(private http: HttpClient) {}

  checkIn(data: { visitRequestId?: number, qrCodeToken?: string, badgeNumber?: string }): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.API_URL}/check-in`, data);
  }

  checkOut(id: number): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.API_URL}/${id}/check-out`, {});
  }

  getActiveVisitors(): Observable<ApiResponse<VisitLog[]>> {
    return this.http.get<ApiResponse<VisitLog[]>>(`${this.API_URL}/active`);
  }
}
