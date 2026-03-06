import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/vms.models';
import { environment } from '../../../environments/environment';

export interface Visitor {
  id?: number;
  name: string;
  email: string;
  mobileNumber: string;
  companyName?: string;
  idProofType: string;
  idProofNumber: string;
  photoPath?: string;
}

@Injectable({
  providedIn: 'root'
})
export class VisitorService {
  private readonly API_URL = `${environment.apiUrl}/visitors`;

  constructor(private http: HttpClient) {}

  searchVisitors(query: string): Observable<ApiResponse<Visitor[]>> {
    return this.http.get<ApiResponse<Visitor[]>>(`${this.API_URL}/search?keyword=${encodeURIComponent(query)}`);
  }

  getVisitorById(id: number): Observable<ApiResponse<Visitor>> {
    return this.http.get<ApiResponse<Visitor>>(`${this.API_URL}/${id}`);
  }

  uploadPhoto(visitorId: number, file: File): Observable<ApiResponse<Visitor>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ApiResponse<Visitor>>(`${this.API_URL}/${visitorId}/photo`, formData);
  }

  getPhotoUrl(visitorId: number): string {
    return `${this.API_URL}/${visitorId}/photo`;
  }
}
