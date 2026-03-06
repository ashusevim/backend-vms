import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZXingScannerModule } from '@zxing/ngx-scanner';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { VisitLogService, VisitLog } from '../../core/services/visit-log.service';
import { BarcodeFormat } from '@zxing/library';

@Component({
  selector: 'app-security-dashboard',
  standalone: true,
  imports: [CommonModule, ZXingScannerModule, NavbarComponent],
  template: `
    <div class="min-h-screen bg-slate-900 text-white">
      <app-navbar></app-navbar>

      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-12">
        <div class="grid lg:grid-cols-2 gap-12">
          
          <!-- Scanner Section -->
          <div class="space-y-8">
            <header>
              <h1 class="text-3xl font-black">QR Security Scanner</h1>
              <p class="text-slate-400">Scan visitor QR code to instantly verify entry</p>
            </header>

            <div class="relative aspect-square max-w-md mx-auto bg-black rounded-[3rem] overflow-hidden border-4 border-slate-800 shadow-2xl group">
              <zxing-scanner
                [formats]="allowedFormats"
                (scanSuccess)="onScanSuccess($event)"
                [enable]="scannerEnabled"
                class="w-full h-full object-cover">
              </zxing-scanner>
              
              <!-- Scanner overlay -->
              <div class="absolute inset-x-8 top-1/4 bottom-1/4 border-2 border-primary-500 rounded-2xl animate-pulse flex items-center justify-center">
                 <div class="w-full h-0.5 bg-primary-500 shadow-[0_0_15px_#0ea5e9] animate-scan"></div>
              </div>

              <div class="absolute bottom-8 inset-x-0 flex justify-center">
                <button (click)="scannerEnabled = !scannerEnabled" 
                  class="px-8 py-3 bg-white text-slate-900 rounded-full font-black shadow-xl hover:scale-105 transition-transform active:scale-95">
                  {{ scannerEnabled ? 'Pause Scanner' : 'Resume Scanner' }}
                </button>
              </div>
            </div>

            <div *ngIf="scanResult()" class="glass p-6 rounded-2xl border-primary-500/30 animate-in zoom-in duration-300">
               <div class="flex items-center gap-4">
                 <div class="w-12 h-12 bg-green-500 rounded-full flex items-center justify-center text-white">
                    <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
                 </div>
                 <div>
                   <h3 class="font-black text-xl">Verification Successful</h3>
                   <p class="text-slate-400">Visitor: {{ scanResult() }}</p>
                 </div>
               </div>
            </div>
            
            <div *ngIf="error()" class="bg-red-500/20 border border-red-500/50 p-6 rounded-2xl text-red-400 font-bold">
              {{ error() }}
            </div>
          </div>

          <!-- Active Board Section -->
          <div class="space-y-8">
            <header class="flex justify-between items-end">
              <div>
                <h2 class="text-2xl font-black">On-Premises Board</h2>
                <p class="text-slate-400">Current active visitors in building</p>
              </div>
              <span class="px-4 py-2 bg-slate-800 rounded-xl text-primary-400 font-black text-xl">
                {{ activeVisitors().length }}
              </span>
            </header>

            <div class="space-y-4 max-h-[600px] overflow-y-auto pr-2 custom-scrollbar">
              <div *ngFor="let visitor of activeVisitors()" 
                class="bg-slate-800/50 p-6 rounded-3xl border border-slate-700 hover:border-primary-500/50 transition-all group">
                <div class="flex justify-between items-start">
                  <div class="flex gap-4">
                    <div class="w-12 h-12 bg-slate-700 rounded-2xl flex items-center justify-center font-black text-primary-400 group-hover:bg-primary-500 group-hover:text-white transition-colors">
                      {{ visitor.visitorName[0] }}
                    </div>
                    <div>
                      <p class="font-black text-lg">{{ visitor.visitorName }}</p>
                      <p class="text-xs text-slate-500 font-bold uppercase tracking-widest">Host: {{ visitor.associateName }}</p>
                    </div>
                  </div>
                  <button (click)="checkOut(visitor.id)" 
                    class="px-4 py-2 bg-red-500/10 text-red-500 border border-red-500/30 rounded-xl text-xs font-black uppercase tracking-widest hover:bg-red-500 hover:text-white transition-all">
                    Check Out
                  </button>
                </div>
                <div class="mt-4 flex items-center gap-4 text-xs font-bold text-slate-400">
                  <span class="flex items-center gap-1">
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    IN: {{ visitor.checkInTime | date:'shortTime' }}
                  </span>
                  <span class="px-2 py-0.5 bg-slate-700 rounded text-[10px] text-slate-300">
                    Badge: {{ visitor.badgeNumber || 'N/A' }}
                  </span>
                </div>
              </div>
              
              <div *ngIf="activeVisitors().length === 0" class="text-center py-20 opacity-30">
                <svg class="w-20 h-20 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
                <p class="font-black text-xl">No active visitors</p>
              </div>
            </div>
          </div>

        </div>
      </main>
    </div>
  `,
  styles: [`
    @keyframes scan {
      0%, 100% { transform: translateY(0); }
      50% { transform: translateY(100%); }
    }
    .animate-scan {
      animation: scan 2.5s ease-in-out infinite;
    }
    .custom-scrollbar::-webkit-scrollbar { width: 6px; }
    .custom-scrollbar::-webkit-scrollbar-thumb { background: #334155; border-radius: 10px; }
  `]
})
export class SecurityDashboardComponent implements OnInit {
  activeVisitors = signal<VisitLog[]>([]);
  scanResult = signal<string | null>(null);
  error = signal<string | null>(null);
  scannerEnabled = true;
  allowedFormats = [BarcodeFormat.QR_CODE];

  constructor(private visitLogService: VisitLogService) {}

  ngOnInit(): void {
    this.loadActiveVisitors();
  }

  loadActiveVisitors() {
    this.visitLogService.getActiveVisitors().subscribe(res => {
      this.activeVisitors.set(res.data);
    });
  }

  onScanSuccess(result: string) {
    this.scannerEnabled = false;
    this.visitLogService.checkIn({ qrCodeToken: result, badgeNumber: 'AUTO-' + Math.floor(Math.random() * 1000) }).subscribe({
      next: (res) => {
        this.scanResult.set(res.message);
        this.error.set(null);
        this.loadActiveVisitors();
        setTimeout(() => {
          this.scanResult.set(null);
          this.scannerEnabled = true;
        }, 3000);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Verification failed');
        setTimeout(() => {
          this.error.set(null);
          this.scannerEnabled = true;
        }, 3000);
      }
    });
  }

  checkOut(id: number) {
    if (confirm('Confirm check-out for this visitor?')) {
      this.visitLogService.checkOut(id).subscribe(() => this.loadActiveVisitors());
    }
  }
}
