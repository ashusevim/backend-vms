import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="fixed w-full z-50 transition-all duration-300 glass" [class.py-4]="!isScrolled" [class.py-2]="isScrolled">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <a routerLink="/" class="flex items-center gap-2 cursor-pointer">
            <div class="w-10 h-10 bg-primary-600 rounded-lg flex items-center justify-center text-white font-bold text-xl shadow-lg transform rotate-3 hover:rotate-0 transition-transform">
              V
            </div>
            <span class="text-2xl font-black tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-primary-700 to-primary-500">
              VMS <span class="text-slate-900 font-light italic"></span>
            </span>
          </a>
          
          <div class="hidden md:flex items-center space-x-8">
            <a routerLink="/" class="text-slate-600 hover:text-primary-600 font-medium transition-colors">Home</a>
            <a href="#about" class="text-slate-600 hover:text-primary-600 font-medium transition-colors">About</a>
            <a routerLink="/contact" class="text-slate-600 hover:text-primary-600 font-medium transition-colors">Contact</a>

            <!-- Logged-in user info -->
            <ng-container *ngIf="authService.currentUser() as user; else loginBtn">
              <div class="flex items-center gap-3">
                <div class="flex items-center gap-2">
                  <div class="w-8 h-8 bg-primary-600 rounded-full flex items-center justify-center text-white font-bold text-sm">
                    {{ user.name.charAt(0).toUpperCase() }}
                  </div>
                  <div class="flex flex-col leading-tight">
                    <span class="text-sm font-semibold text-slate-800">{{ user.name }}</span>
                    <span class="text-xs font-medium text-primary-600">{{ user.role }}</span>
                  </div>
                </div>
                <button (click)="logout()" class="bg-red-500 text-white px-4 py-1.5 rounded-full text-sm font-bold shadow-md hover:bg-red-600 hover:shadow-lg transition-all active:scale-95">
                  Logout
                </button>
              </div>
            </ng-container>
            <ng-template #loginBtn>
              <button routerLink="/login" class="bg-primary-600 text-white px-6 py-2 rounded-full font-bold shadow-md hover:bg-primary-700 hover:shadow-lg transition-all active:scale-95">
                Login
              </button>
            </ng-template>
          </div>

          <div class="md:hidden flex items-center">
            <button (click)="toggleMenu()" class="text-slate-600 focus:outline-none">
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path *ngIf="!isMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                <path *ngIf="isMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Mobile Menu -->
      <div *ngIf="isMenuOpen" class="md:hidden bg-white border-t border-slate-100 p-4 space-y-4 animate-in slide-in-from-top duration-300">
        <a routerLink="/" class="block text-slate-600 hover:text-primary-600 font-medium" (click)="isMenuOpen = false">Home</a>
        <a href="#about" class="block text-slate-600 hover:text-primary-600 font-medium" (click)="isMenuOpen = false">About</a>
        <a routerLink="/contact" class="block text-slate-600 hover:text-primary-600 font-medium" (click)="isMenuOpen = false">Contact</a>

        <!-- Mobile: logged-in user info -->
        <ng-container *ngIf="authService.currentUser() as user; else mobileLoginBtn">
          <div class="flex items-center justify-between pt-2 border-t border-slate-100">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 bg-primary-600 rounded-full flex items-center justify-center text-white font-bold text-sm">
                {{ user.name.charAt(0).toUpperCase() }}
              </div>
              <div class="flex flex-col leading-tight">
                <span class="text-sm font-semibold text-slate-800">{{ user.name }}</span>
                <span class="text-xs font-medium text-primary-600">{{ user.role }}</span>
              </div>
            </div>
            <button (click)="logout(); isMenuOpen = false" class="bg-red-500 text-white px-4 py-1.5 rounded-full text-sm font-bold shadow-md hover:bg-red-600 transition-all active:scale-95">
              Logout
            </button>
          </div>
        </ng-container>
        <ng-template #mobileLoginBtn>
          <button routerLink="/login" class="w-full bg-primary-600 text-white px-6 py-2 rounded-full font-bold shadow-md" (click)="isMenuOpen = false">
            Login
          </button>
        </ng-template>
      </div>
    </nav>
  `,
  styles: []
})
export class NavbarComponent {
  authService = inject(AuthService);
  private router = inject(Router);
  isScrolled = false;
  isMenuOpen = false;

  constructor() {
    window.addEventListener('scroll', () => {
      this.isScrolled = window.scrollY > 20;
    });
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
