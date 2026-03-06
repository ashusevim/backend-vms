import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { authGuard } from './core/guards/auth.guard';
import { Role } from './core/models/vms.models';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { 
    path: 'login', 
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) 
  },
  { 
    path: 'register', 
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) 
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/admin-dashboard.component').then(m => m.AdminDashboardComponent),
    canActivate: [authGuard],
    data: { role: Role.ADMIN }
  },
  {
    path: 'admin/visit-requests',
    loadComponent: () => import('./features/admin/admin-visit-requests.component').then(m => m.AdminVisitRequestsComponent),
    canActivate: [authGuard],
    data: { role: Role.ADMIN }
  },
  {
    path: 'associate',
    loadComponent: () => import('./features/associate/associate-dashboard.component').then(m => m.AssociateDashboardComponent),
    canActivate: [authGuard],
    data: { role: Role.ASSOCIATE }
  },
  {
    path: 'security',
    loadComponent: () => import('./features/security/security-dashboard.component').then(m => m.SecurityDashboardComponent),
    canActivate: [authGuard],
    data: { role: Role.SECURITY }
  },
  {
    path: 'contact',
    loadComponent: () => import('./features/contact/contact.component').then(m => m.ContactComponent)
  },
  { path: '**', redirectTo: '' }
];
