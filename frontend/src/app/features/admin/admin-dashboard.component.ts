import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardResponse } from '../../core/services/dashboard.service';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { DateTime } from 'luxon';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, NgChartsModule, NavbarComponent],
  template: `
    <div class="min-h-screen bg-slate-50">
      <app-navbar></app-navbar>
      
      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-12">
        <header class="flex justify-between items-center mb-8">
          <div>
            <h1 class="text-3xl font-black text-slate-900">Admin Command Center</h1>
            <p class="text-slate-500">Real-time overview of visitor operations</p>
          </div>
          <div class="flex items-center gap-4">
            <a href="/admin/visit-requests" class="px-5 py-2.5 bg-primary-600 text-white rounded-xl font-bold text-sm hover:bg-primary-700 transition-colors shadow-md flex items-center gap-2">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" /></svg>
              Manage Visit Requests
            </a>
            <div class="glass px-4 py-2 rounded-xl flex items-center gap-2">
              <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
              <span class="text-sm font-bold text-slate-700">Live Traffic Tracking</span>
            </div>
          </div>
        </header>

        <!-- Stats Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 hover:shadow-md transition-shadow">
            <p class="text-slate-400 text-xs font-black uppercase tracking-widest">Total Visitors <span class="text-[10px] text-slate-300">({{ selectedDate().toFormat('dd MMM yyyy') }})</span></p>
            <div class="flex items-end justify-between mt-2">
              <h3 class="text-4xl font-black text-slate-900">{{ dateStats()?.totalVisitors ?? stats()?.totalVisitors ?? 0 }}</h3>
              <span class="px-2 py-1 bg-blue-100 text-blue-600 text-xs font-bold rounded-lg">{{ selectedDate().toFormat('dd MMM') }}</span>
            </div>
          </div>
          
          <div class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 hover:shadow-md transition-shadow">
            <p class="text-slate-400 text-xs font-black uppercase tracking-widest text-amber-500">Pending Approvals <span class="text-[10px] text-slate-300">({{ selectedDate().toFormat('dd MMM yyyy') }})</span></p>
            <div class="flex items-end justify-between mt-2">
              <h3 class="text-4xl font-black text-slate-900">{{ dateStats()?.totalPending ?? stats()?.totalPending ?? 0 }}</h3>
              <div class="w-10 h-10 bg-amber-50 rounded-xl flex items-center justify-center text-amber-600">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              </div>
            </div>
          </div>

          <div class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 hover:shadow-md transition-shadow">
            <p class="text-slate-400 text-xs font-black uppercase tracking-widest text-green-600">Approved <span class="text-[10px] text-slate-300">({{ selectedDate().toFormat('dd MMM yyyy') }})</span></p>
            <div class="flex items-end justify-between mt-2">
              <h3 class="text-4xl font-black text-slate-900">{{ dateStats()?.totalApprovals ?? stats()?.totalApprovals ?? 0 }}</h3>
              <div class="w-10 h-10 bg-green-50 rounded-xl flex items-center justify-center text-green-600">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
              </div>
            </div>
          </div>

          <div class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 hover:shadow-md transition-shadow">
            <p class="text-slate-400 text-xs font-black uppercase tracking-widest text-red-500">Rejections <span class="text-[10px] text-slate-300">({{ selectedDate().toFormat('dd MMM yyyy') }})</span></p>
            <div class="flex items-end justify-between mt-2">
              <h3 class="text-4xl font-black text-slate-900">{{ dateStats()?.totalRejected ?? stats()?.totalRejected ?? 0 }}</h3>
              <div class="w-10 h-10 bg-red-50 rounded-xl flex items-center justify-center text-red-600">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              </div>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- Main Chart -->
          <div class="lg:col-span-2 bg-white p-8 rounded-[2rem] shadow-sm border border-slate-100">
            <h3 class="text-xl font-bold mb-6">Visiting Trends (Weekly)</h3>
            <div class="h-[300px]">
              <canvas baseChart
                [data]="lineChartData"
                [options]="lineChartOptions"
                [type]="'line'">
              </canvas>
            </div>
          </div>

          <!-- Pie Chart -->
          <div class="bg-white p-8 rounded-[2rem] shadow-sm border border-slate-100">
            <h3 class="text-xl font-bold mb-6">Department Breakdown</h3>
            <div class="h-[300px] flex items-center justify-center">
              <canvas baseChart
                [data]="pieChartData"
                [options]="pieChartOptions"
                [type]="'doughnut'">
              </canvas>
            </div>
          </div>
        </div>

        <!-- Calendar & Daily Stats -->
        <div class="mt-8 grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="bg-white p-8 rounded-[2rem] shadow-sm border border-slate-100">
            <div class="flex justify-between items-center mb-6">
              <h3 class="text-xl font-bold">Interactive Calendar</h3>
              <div class="flex gap-1">
                <button (click)="prevMonth()" class="p-1.5 rounded-lg hover:bg-slate-100 transition-colors">
                  <svg class="w-5 h-5 text-slate-600" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/></svg>
                </button>
                <span class="text-sm font-bold text-slate-700 px-2 flex items-center">{{ currentMonthLabel() }}</span>
                <button (click)="nextMonth()" class="p-1.5 rounded-lg hover:bg-slate-100 transition-colors">
                  <svg class="w-5 h-5 text-slate-600" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
                </button>
              </div>
            </div>
            <div class="calendar-grid grid grid-cols-7 gap-2">
              <div *ngFor="let day of weekDays" class="text-center text-xs font-black text-slate-400 uppercase mb-2">{{day}}</div>
              <button *ngFor="let date of calendarDates" 
                (click)="selectDate(date)"
                [class.bg-primary-600]="isSelected(date)"
                [class.text-white]="isSelected(date)"
                [class.text-slate-400]="!isCurrentMonth(date)"
                [class.ring-2]="isToday(date)"
                [class.ring-primary-300]="isToday(date)"
                class="aspect-square flex items-center justify-center rounded-xl hover:bg-primary-100 transition-colors text-sm font-bold">
                {{ date.day }}
              </button>
            </div>
          </div>

          <div class="lg:col-span-2 bg-slate-900 text-white p-8 rounded-[2rem] shadow-xl relative overflow-hidden">
             <div class="absolute top-0 right-0 p-8 opacity-10">
               <svg class="w-32 h-32" fill="currentColor" viewBox="0 0 20 20"><path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z"/></svg>
             </div>
             <div class="relative z-10">
               <h3 class="text-2xl font-black mb-2">{{ selectedDateLabel() }}</h3>
               <p class="text-slate-400 mb-8">Visitor statistics for the selected date</p>
               
               <div *ngIf="isLoadingDateStats" class="flex items-center justify-center py-12">
                 <span class="animate-spin border-4 border-white border-t-transparent rounded-full w-8 h-8"></span>
               </div>

               <div *ngIf="!isLoadingDateStats" class="grid grid-cols-2 md:grid-cols-4 gap-6">
                 <div>
                   <p class="text-xs font-bold text-slate-500 uppercase tracking-widest">Total Visits</p>
                   <p class="text-3xl font-black mt-1">{{ dateStats()?.visitorsToday || 0 }}</p>
                 </div>
                 <div>
                   <p class="text-xs font-bold text-green-400 uppercase tracking-widest">Approved</p>
                   <p class="text-3xl font-black mt-1 text-green-400">{{ dateStats()?.approvedCount || 0 }}</p>
                 </div>
                 <div>
                   <p class="text-xs font-bold text-amber-400 uppercase tracking-widest">Pending</p>
                   <p class="text-3xl font-black mt-1 text-amber-400">{{ dateStats()?.pendingCount || 0 }}</p>
                 </div>
                 <div>
                   <p class="text-xs font-bold text-red-400 uppercase tracking-widest">Rejected</p>
                   <p class="text-3xl font-black mt-1 text-red-400">{{ dateStats()?.rejectedCount || 0 }}</p>
                 </div>
               </div>

               <!-- Weekly Stats -->
               <div class="mt-8 pt-6 border-t border-slate-700">
                 <h4 class="text-sm font-black text-slate-400 uppercase tracking-widest mb-4">Weekly Overview ({{ weekRangeLabel() }})</h4>
                 <div *ngIf="!isLoadingDateStats" class="grid grid-cols-2 md:grid-cols-4 gap-6">
                   <div>
                     <p class="text-xs font-bold text-slate-500 uppercase tracking-widest">This Week</p>
                     <p class="text-3xl font-black mt-1">{{ weekStats()?.visitorsThisWeek || 0 }}</p>
                   </div>
                   <div>
                     <p class="text-xs font-bold text-green-400 uppercase tracking-widest">Approved</p>
                     <p class="text-3xl font-black mt-1 text-green-400">{{ weekStats()?.approvedCount || 0 }}</p>
                   </div>
                   <div>
                     <p class="text-xs font-bold text-amber-400 uppercase tracking-widest">Pending</p>
                     <p class="text-3xl font-black mt-1 text-amber-400">{{ weekStats()?.pendingCount || 0 }}</p>
                   </div>
                   <div>
                     <p class="text-xs font-bold text-red-400 uppercase tracking-widest">Rejected</p>
                     <p class="text-3xl font-black mt-1 text-red-400">{{ weekStats()?.rejectedCount || 0 }}</p>
                   </div>
                 </div>
               </div>
             </div>
          </div>
        </div>
      </main>
    </div>
  `,
  styles: [`
    .calendar-grid button.active {
      @apply bg-primary-600 text-white;
    }
  `]
})
export class AdminDashboardComponent implements OnInit {
  stats = signal<DashboardResponse | null>(null);
  dateStats = signal<DashboardResponse | null>(null);
  weekStats = signal<DashboardResponse | null>(null);
  selectedDate = signal<DateTime>(DateTime.now());
  calendarMonth = signal<DateTime>(DateTime.now());
  isLoadingDateStats = false;
  
  weekDays = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
  calendarDates: DateTime[] = [];

  constructor(private dashboardService: DashboardService) {
    this.generateCalendar();
  }

  ngOnInit(): void {
    this.loadStats();
    this.loadDateStats(this.selectedDate());
    this.loadWeekStats(this.selectedDate());
  }

  loadStats() {
    this.dashboardService.getStats().subscribe(res => {
      this.stats.set(res.data);
      this.updateCharts(res.data);
    });
  }

  loadDateStats(date: DateTime) {
    this.isLoadingDateStats = true;
    const dateStr = date.toFormat('yyyy-MM-dd');
    this.dashboardService.getAnalytics(dateStr, dateStr).subscribe({
      next: (res) => {
        this.dateStats.set(res.data);
        this.isLoadingDateStats = false;
      },
      error: () => this.isLoadingDateStats = false
    });
  }

  loadWeekStats(date: DateTime) {
    const weekStart = date.startOf('week');
    const weekEnd = date.endOf('week');
    const from = weekStart.toFormat('yyyy-MM-dd');
    const to = weekEnd.toFormat('yyyy-MM-dd');
    this.dashboardService.getAnalytics(from, to).subscribe({
      next: (res) => {
        this.weekStats.set(res.data);
        // Update the weekly trend chart with real data
        if (res.data.dailyVisitorCounts) {
          const labels = Object.keys(res.data.dailyVisitorCounts);
          const data = Object.values(res.data.dailyVisitorCounts);
          this.lineChartData = {
            labels,
            datasets: [{
              data,
              label: 'Visitor Count',
              borderColor: '#0ea5e9',
              backgroundColor: 'rgba(14, 165, 233, 0.1)',
              fill: 'origin',
              tension: 0.4
            }]
          };
        }
      }
    });
  }

  // Chart Logic
  public lineChartData: ChartData<'line'> = {
    labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    datasets: [{
      data: [65, 59, 80, 81, 56, 55, 40],
      label: 'Visitor Count',
      borderColor: '#0ea5e9',
      backgroundColor: 'rgba(14, 165, 233, 0.1)',
      fill: 'origin',
      tension: 0.4
    }]
  };

  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      y: { grid: { display: false } },
      x: { grid: { display: false } }
    }
  };

  public pieChartData: ChartData<'doughnut'> = {
    labels: ['IT', 'HR', 'Finance', 'Engineering'],
    datasets: [{
      data: [35, 15, 20, 30],
      backgroundColor: ['#0ea5e9', '#6366f1', '#f59e0b', '#ec4899']
    }]
  };

  public pieChartOptions: ChartConfiguration['options'] = {
     responsive: true,
     maintainAspectRatio: false,
     plugins: { legend: { position: 'bottom' } }
  };

  updateCharts(data: DashboardResponse) {
    if (data.visitorsByDepartment) {
      this.pieChartData.labels = Object.keys(data.visitorsByDepartment);
      this.pieChartData.datasets[0].data = Object.values(data.visitorsByDepartment);
    }
  }

  // Calendar Logic
  generateCalendar() {
    const month = this.calendarMonth();
    const startOfMonth = month.startOf('month').startOf('week');
    const endOfMonth = month.endOf('month').endOf('week');
    
    let current = startOfMonth;
    const dates = [];
    while (current <= endOfMonth) {
      dates.push(current);
      current = current.plus({ days: 1 });
    }
    this.calendarDates = dates;
  }

  selectDate(date: DateTime) {
    this.selectedDate.set(date);
    this.loadDateStats(date);
    this.loadWeekStats(date);
  }

  prevMonth() {
    this.calendarMonth.set(this.calendarMonth().minus({ months: 1 }));
    this.generateCalendar();
  }

  nextMonth() {
    this.calendarMonth.set(this.calendarMonth().plus({ months: 1 }));
    this.generateCalendar();
  }

  isSelected(date: DateTime) {
    return date.hasSame(this.selectedDate(), 'day');
  }

  isCurrentMonth(date: DateTime) {
    return date.hasSame(this.calendarMonth(), 'month');
  }

  isToday(date: DateTime) {
    return date.hasSame(DateTime.now(), 'day');
  }

  currentMonthLabel() {
    return this.calendarMonth().toFormat('MMMM yyyy');
  }

  selectedDateLabel() {
    return this.selectedDate().toLocaleString(DateTime.DATE_HUGE);
  }

  weekRangeLabel() {
    const weekStart = this.selectedDate().startOf('week');
    const weekEnd = this.selectedDate().endOf('week');
    return weekStart.toFormat('MMM d') + ' - ' + weekEnd.toFormat('MMM d, yyyy');
  }
}
