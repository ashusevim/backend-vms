import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { trigger, transition, style, animate, query, stagger } from '@angular/animations';
import { AuthService } from '../../core/services/auth.service';
import { Role } from '../../core/models/vms.models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('800ms cubic-bezier(0.35, 0, 0.25, 1)', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('staggerList', [
      transition(':enter', [
        query('.stagger-item', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(100, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ])
  ],
  template: `
    <div class="min-h-screen">
      <app-navbar></app-navbar>

      <!-- Hero Section -->
      <section class="relative pt-32 pb-20 lg:pt-48 lg:pb-32 overflow-hidden bg-slate-50">
        <!-- Abstract Background Shapes -->
        <div class="absolute top-0 right-0 -translate-y-1/2 translate-x-1/4 w-[600px] h-[600px] bg-primary-200 rounded-full blur-3xl opacity-30"></div>
        <div class="absolute bottom-0 left-0 translate-y-1/2 -translate-x-1/4 w-[400px] h-[400px] bg-primary-300 rounded-full blur-3xl opacity-20"></div>

        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div class="text-center" @fadeInUp>
            <h1 class="text-5xl lg:text-7xl font-black text-slate-900 mb-6 leading-tight">
              NxT-LvL <br/>
              <span class="bg-clip-text text-transparent bg-gradient-to-r from-primary-600 to-cyan-500">
                Visitor Management
              </span>
            </h1>
            <p class="text-xl text-slate-600 max-w-2xl mx-auto mb-10 leading-relaxed">
              Seamless. Secure. Smart. Elevate your workplace safety and guest experience with VMS Intellect's visitor tracking platform.
            </p>
            
            <div class="flex flex-col sm:flex-row justify-center gap-4 mt-8">
              <button routerLink="/register" [queryParams]="{role: 'ADMIN'}" 
                class="group px-8 py-4 bg-slate-900 text-white rounded-2xl font-bold flex items-center justify-center gap-2 hover:bg-slate-800 transition-all hover:scale-105 active:scale-95 shadow-xl">
                <span>Admin Gateway</span>
                <svg class="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                </svg>
              </button>
              <button routerLink="/register" [queryParams]="{role: 'ASSOCIATE'}" 
                class="group px-8 py-4 bg-white text-slate-900 border-2 border-slate-200 rounded-2xl font-bold flex items-center justify-center gap-2 hover:border-primary-500 hover:text-primary-600 transition-all hover:scale-105 active:scale-95 shadow-lg">
                <span>Associate Portal</span>
                <svg class="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- About Section -->
      <section id="about" class="py-24 bg-white">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="grid lg:grid-cols-2 gap-16 items-center">
            <div @fadeInUp>
              <h2 class="text-4xl font-bold text-slate-900 mb-6">Innovative Solutions for <br/> Safety & Efficiency</h2>
              <div class="space-y-6 text-lg text-slate-600 leading-relaxed">
                <p>VMS Intellect is more than just a digital logbook. It's a comprehensive security ecosystem designed to streamline visitor workflows while maintaining the highest standards of data integrity.</p>
                <div class="grid grid-cols-2 gap-6 mt-8" @staggerList>
                  <div class="stagger-item flex items-start gap-3">
                    <div class="p-2 bg-primary-100 text-primary-600 rounded-lg">
                      <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 17h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" /></svg>
                    </div>
                    <div>
                      <h4 class="font-bold text-slate-900">QR Check-in</h4>
                      <p class="text-sm">Contactless entry via intelligent QR scanners.</p>
                    </div>
                  </div>
                  <div class="stagger-item flex items-start gap-3">
                    <div class="p-2 bg-primary-100 text-primary-600 rounded-lg">
                      <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" /></svg>
                    </div>
                    <div>
                      <h4 class="font-bold text-slate-900">Real-time Analytics</h4>
                      <p class="text-sm">Monitor premise traffic with interactive heatmaps.</p>
                    </div>
                  </div>
                  <div class="stagger-item flex items-start gap-3">
                    <div class="p-2 bg-primary-100 text-primary-600 rounded-lg">
                      <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" /></svg>
                    </div>
                    <div>
                      <h4 class="font-bold text-slate-900">Instant Alerts</h4>
                      <p class="text-sm">Immediate notifications for hosts and security.</p>
                    </div>
                  </div>
                  <div class="stagger-item flex items-start gap-3">
                    <div class="p-2 bg-primary-100 text-primary-600 rounded-lg">
                      <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" /></svg>
                    </div>
                    <div>
                      <h4 class="font-bold text-slate-900">Role-Based Access</h4>
                      <p class="text-sm">Granular control for admins, associates, and guards.</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="relative" @fadeInUp>
              <div class="aspect-square bg-gradient-to-br from-primary-400 to-primary-600 rounded-[2rem] shadow-2xl p-8 transform rotate-3">
                <div class="w-full h-full bg-slate-900 rounded-xl p-6 shadow-inner relative overflow-hidden group">
                  <div class="absolute inset-0 bg-primary-600 opacity-20 group-hover:opacity-30 transition-opacity"></div>
                  <div class="relative z-10 border-2 border-dashed border-white border-opacity-30 rounded-lg h-full flex flex-col items-center justify-center">
                    <svg class="w-24 h-24 text-white opacity-50 mb-4 animate-pulse" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 17h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
                    </svg>
                    <p class="text-white font-bold tracking-widest uppercase">System Active</p>
                  </div>
                </div>
              </div>
              <!-- Floating Badge -->
              <div class="absolute -bottom-6 -left-6 bg-white p-6 rounded-2xl shadow-xl border border-slate-100 animate-bounce">
                <div class="flex items-center gap-4">
                  <div class="w-12 h-12 bg-green-100 text-green-600 rounded-full flex items-center justify-center">
                    <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" /></svg>
                  </div>
                  <div>
                    <h5 class="text-xl font-black text-slate-900">100%</h5>
                    <p class="text-xs text-slate-500 uppercase font-bold tracking-wider">Security Rating</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Footer -->
      <footer class="bg-slate-900 text-slate-400 py-12 border-t border-slate-800">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <p>&copy; 2026 VMS Intellect. Elevating Workplace Intelligence.</p>
        </div>
      </footer>
    </div>
  `,
  styles: []
})
export class HomeComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  constructor() {
    const user = this.authService.currentUser();
    if (user) {
      if (user.role === Role.ADMIN) this.router.navigate(['/admin']);
      else if (user.role === Role.ASSOCIATE) this.router.navigate(['/associate']);
      else if (user.role === Role.SECURITY) this.router.navigate(['/security']);
    }
  }
}
