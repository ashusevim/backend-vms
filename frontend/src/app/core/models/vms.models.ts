export enum Role {
  ADMIN = 'ADMIN',
  ASSOCIATE = 'ASSOCIATE',
  SECURITY = 'SECURITY'
}

export enum VisitStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED'
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  mobileNumber?: string;
  department?: string;
  designation?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  name: string;
  email: string;
  role: Role;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
