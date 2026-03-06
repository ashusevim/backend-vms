import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, VisitStatus } from '../models/vms.models';
import { environment } from '../../../environments/environment';

export interface VisitRequest {
  id?: number;
  visitorId?: number;
  associateId: number;
  purpose: string;
  visitDate: string;
  visitTime: string;
  status?: VisitStatus;
  isGroupVisit?: boolean;
  additionalVisitors?: any[];
  newVisitor?: any;
}

export interface VisitRequestResponse {
  id: number;
  purpose: string;
  category: string;
  fromDate: string;
  toDate: string;
  inTime: string;
  outTime: string;
  status: VisitStatus;
  remarks: string;
  qrCodeToken: string;
  groupId: string;
  createdAt: string;
  approvedAt: string;
  visitorId: number;
  visitorName: string;
  visitorMobile: string;
  visitorEmail: string;
  visitorCompany: string;
  associateId: number;
  associateName: string;
  associateDepartment: string;
  approvedByName: string;
}

export interface ApproveRejectRequest {
  status: VisitStatus;
  remarks?: string;
}

@Injectable({
  providedIn: 'root'
})
export class VisitRequestService {
  private readonly API_URL = `${environment.apiUrl}/visit-requests`;

  constructor(private http: HttpClient) {}

  createRequest(request: VisitRequest): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(this.API_URL, request);
  }

  getAllRequests(): Observable<ApiResponse<VisitRequestResponse[]>> {
    return this.http.get<ApiResponse<VisitRequestResponse[]>>(this.API_URL);
  }

  getRequestById(id: number): Observable<ApiResponse<VisitRequestResponse>> {
    return this.http.get<ApiResponse<VisitRequestResponse>>(`${this.API_URL}/${id}`);
  }

  getRequestsByStatus(status: VisitStatus): Observable<ApiResponse<VisitRequestResponse[]>> {
    return this.http.get<ApiResponse<VisitRequestResponse[]>>(`${this.API_URL}/status/${status}`);
  }

  getAssociateRequests(associateId: number): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.API_URL}/associate/${associateId}`);
  }

  approveReject(id: number, request: ApproveRejectRequest): Observable<ApiResponse<VisitRequestResponse>> {
    return this.http.put<ApiResponse<VisitRequestResponse>>(`${this.API_URL}/${id}/approve-reject`, request);
  }

  cancelRequest(id: number): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.API_URL}/${id}/cancel`, {});
  }
}
