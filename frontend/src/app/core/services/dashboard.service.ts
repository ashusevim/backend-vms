import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/vms.models';
import { environment } from '../../../environments/environment';

export interface AssociateVisitCount {
  associateName: string;
  visitCount: number;
}

export interface DashboardResponse {
  totalApprovals: number;
  totalPending: number;
  totalRejected: number;
  totalVisitors: number;
  visitorsToday: number;
  visitorsThisWeek: number;
  approvedCount: number;
  pendingCount: number;
  rejectedCount: number;
  visitorsByDepartment: { [key: string]: number };
  peakVisitingHours: { [key: number]: number };
  mostVisitedAssociates: AssociateVisitCount[];
  dailyVisitorCounts: { [key: string]: number };
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly API_URL = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getStats(): Observable<ApiResponse<DashboardResponse>> {
    return this.http.get<ApiResponse<DashboardResponse>>(`${this.API_URL}/stats`);
  }

  getAnalytics(from: string, to: string): Observable<ApiResponse<DashboardResponse>> {
    const params = new HttpParams().set('from', from).set('to', to);
    return this.http.get<ApiResponse<DashboardResponse>>(`${this.API_URL}/analytics`, { params });
  }
}
