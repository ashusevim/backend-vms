import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { VisitRequestService, VisitRequestResponse, ApproveRejectRequest } from '../../core/services/visit-request.service';
import { VisitStatus } from '../../core/models/vms.models';

@Component({
  selector: 'app-admin-visit-requests',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  template: `
    <div class="min-h-screen bg-slate-50">
      <app-navbar></app-navbar>

      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-12">
        <header class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
          <div>
            <h1 class="text-3xl font-black text-slate-900">Visit Requests</h1>
            <p class="text-slate-500">Review, approve, or reject visitor requests</p>
          </div>
          <a href="/admin" class="text-primary-600 font-bold text-sm hover:underline flex items-center gap-1">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
            Back to Dashboard
          </a>
        </header>

        <!-- Status Filter Tabs -->
        <div class="flex flex-wrap gap-2 mb-6">
          <button *ngFor="let tab of statusTabs"
            (click)="activeFilter.set(tab.value)"
            [class]="activeFilter() === tab.value
              ? 'px-5 py-2.5 rounded-full text-sm font-bold transition-all shadow-md ' + tab.activeClass
              : 'px-5 py-2.5 rounded-full text-sm font-bold transition-all bg-white border border-slate-200 text-slate-600 hover:border-slate-300'">
            {{ tab.label }}
            <span class="ml-1.5 text-xs opacity-75">({{ getCountForStatus(tab.value) }})</span>
          </button>
        </div>

        <!-- Loading State -->
        <div *ngIf="loading()" class="flex justify-center py-20">
          <div class="w-10 h-10 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin"></div>
        </div>

        <!-- Empty State -->
        <div *ngIf="!loading() && filteredRequests().length === 0" class="bg-white rounded-3xl shadow-sm border border-slate-100 p-16 text-center">
          <svg class="w-16 h-16 mx-auto text-slate-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <p class="text-slate-500 text-lg font-medium">No {{ activeFilter() === 'ALL' ? '' : activeFilter().toLowerCase() }} requests found</p>
        </div>

        <!-- Requests Table -->
        <div *ngIf="!loading() && filteredRequests().length > 0" class="bg-white rounded-3xl shadow-sm border border-slate-100 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-slate-100">
                  <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Visitor</th>
                  <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Purpose</th>
                  <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Host</th>
                  <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Date</th>
                  <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Status</th>
                  <th class="text-right px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let req of filteredRequests()" 
                  class="border-b border-slate-50 hover:bg-slate-50/50 transition-colors">
                  <td class="px-6 py-4">
                    <div>
                      <p class="font-bold text-slate-900">{{ req.visitorName }}</p>
                      <p class="text-xs text-slate-400">{{ req.visitorEmail }}</p>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <span class="text-sm text-slate-700">{{ req.purpose }}</span>
                    <span *ngIf="req.category" class="ml-2 text-xs bg-slate-100 text-slate-500 px-2 py-0.5 rounded-full">{{ req.category }}</span>
                  </td>
                  <td class="px-6 py-4">
                    <p class="text-sm font-medium text-slate-700">{{ req.associateName }}</p>
                    <p class="text-xs text-slate-400">{{ req.associateDepartment }}</p>
                  </td>
                  <td class="px-6 py-4 text-sm text-slate-600">
                    {{ req.fromDate }}
                    <span *ngIf="req.toDate && req.toDate !== req.fromDate" class="text-slate-400"> &rarr; {{ req.toDate }}</span>
                  </td>
                  <td class="px-6 py-4">
                    <span [class]="getStatusBadgeClass(req.status)">{{ req.status }}</span>
                  </td>
                  <td class="px-6 py-4 text-right">
                    <div class="flex items-center justify-end gap-2">
                      <button (click)="openDetail(req)"
                        class="px-3 py-1.5 text-xs font-bold text-primary-600 bg-primary-50 rounded-lg hover:bg-primary-100 transition-colors">
                        View
                      </button>
                      <button *ngIf="req.status === 'PENDING'" (click)="openApproveReject(req, 'APPROVED')"
                        class="px-3 py-1.5 text-xs font-bold text-green-600 bg-green-50 rounded-lg hover:bg-green-100 transition-colors">
                        Approve
                      </button>
                      <button *ngIf="req.status === 'PENDING'" (click)="openApproveReject(req, 'REJECTED')"
                        class="px-3 py-1.5 text-xs font-bold text-red-600 bg-red-50 rounded-lg hover:bg-red-100 transition-colors">
                        Reject
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </main>

      <!-- Detail Modal -->
      <div *ngIf="selectedRequest()" class="fixed inset-0 z-50 flex items-center justify-center p-4" (click)="closeModals()">
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm"></div>
        <div class="bg-white rounded-3xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto relative z-10" (click)="$event.stopPropagation()">
          <div class="p-8">
            <div class="flex justify-between items-start mb-6">
              <div>
                <h2 class="text-2xl font-black text-slate-900">Visit Request #{{ selectedRequest()!.id }}</h2>
                <span [class]="getStatusBadgeClass(selectedRequest()!.status) + ' mt-2 inline-block'">{{ selectedRequest()!.status }}</span>
              </div>
              <button (click)="closeModals()" class="w-10 h-10 rounded-xl bg-slate-100 flex items-center justify-center hover:bg-slate-200 transition-colors">
                <svg class="w-5 h-5 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
              </button>
            </div>

            <div class="grid md:grid-cols-2 gap-6">
              <!-- Visitor Info -->
              <div class="bg-slate-50 rounded-2xl p-6 space-y-3">
                <h3 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-4">Visitor Information</h3>
                <div>
                  <p class="text-xs text-slate-400">Name</p>
                  <p class="font-bold text-slate-900">{{ selectedRequest()!.visitorName }}</p>
                </div>
                <div>
                  <p class="text-xs text-slate-400">Email</p>
                  <p class="font-medium text-slate-700">{{ selectedRequest()!.visitorEmail }}</p>
                </div>
                <div>
                  <p class="text-xs text-slate-400">Mobile</p>
                  <p class="font-medium text-slate-700">{{ selectedRequest()!.visitorMobile || 'N/A' }}</p>
                </div>
                <div>
                  <p class="text-xs text-slate-400">Company</p>
                  <p class="font-medium text-slate-700">{{ selectedRequest()!.visitorCompany || 'N/A' }}</p>
                </div>
              </div>

              <!-- Visit Details -->
              <div class="bg-slate-50 rounded-2xl p-6 space-y-3">
                <h3 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-4">Visit Details</h3>
                <div>
                  <p class="text-xs text-slate-400">Purpose</p>
                  <p class="font-bold text-slate-900">{{ selectedRequest()!.purpose }}</p>
                </div>
                <div *ngIf="selectedRequest()!.category">
                  <p class="text-xs text-slate-400">Category</p>
                  <p class="font-medium text-slate-700">{{ selectedRequest()!.category }}</p>
                </div>
                <div>
                  <p class="text-xs text-slate-400">Date</p>
                  <p class="font-medium text-slate-700">
                    {{ selectedRequest()!.fromDate }}
                    <span *ngIf="selectedRequest()!.toDate && selectedRequest()!.toDate !== selectedRequest()!.fromDate">
                      &rarr; {{ selectedRequest()!.toDate }}
                    </span>
                  </p>
                </div>
                <div>
                  <p class="text-xs text-slate-400">Time</p>
                  <p class="font-medium text-slate-700">{{ selectedRequest()!.inTime || 'N/A' }} - {{ selectedRequest()!.outTime || 'N/A' }}</p>
                </div>
              </div>

              <!-- Host Info -->
              <div class="bg-slate-50 rounded-2xl p-6 space-y-3">
                <h3 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-4">Host (Associate)</h3>
                <div>
                  <p class="text-xs text-slate-400">Name</p>
                  <p class="font-bold text-slate-900">{{ selectedRequest()!.associateName }}</p>
                </div>
                <div>
                  <p class="text-xs text-slate-400">Department</p>
                  <p class="font-medium text-slate-700">{{ selectedRequest()!.associateDepartment || 'N/A' }}</p>
                </div>
              </div>

              <!-- Approval Info (if approved/rejected) -->
              <div *ngIf="selectedRequest()!.status !== 'PENDING'" class="bg-slate-50 rounded-2xl p-6 space-y-3">
                <h3 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-4">Approval Info</h3>
                <div>
                  <p class="text-xs text-slate-400">Actioned By</p>
                  <p class="font-bold text-slate-900">{{ selectedRequest()!.approvedByName || 'N/A' }}</p>
                </div>
                <div>
                  <p class="text-xs text-slate-400">Actioned At</p>
                  <p class="font-medium text-slate-700">{{ selectedRequest()!.approvedAt || 'N/A' }}</p>
                </div>
                <div *ngIf="selectedRequest()!.remarks">
                  <p class="text-xs text-slate-400">Remarks</p>
                  <p class="font-medium text-slate-700">{{ selectedRequest()!.remarks }}</p>
                </div>
              </div>

              <!-- Group Info -->
              <div *ngIf="selectedRequest()!.groupId" class="md:col-span-2 bg-amber-50 rounded-2xl p-6">
                <h3 class="text-xs font-black text-amber-600 uppercase tracking-widest mb-2">Group Visit</h3>
                <p class="text-sm text-amber-700">This visit is part of a group (ID: {{ selectedRequest()!.groupId }})</p>
              </div>

              <!-- Created At -->
              <div class="md:col-span-2 text-center text-xs text-slate-400 pt-2">
                Request created: {{ selectedRequest()!.createdAt }}
              </div>
            </div>

            <!-- Action Buttons for Pending Requests -->
            <div *ngIf="selectedRequest()!.status === 'PENDING'" class="mt-8 flex gap-3 justify-end">
              <button (click)="openApproveReject(selectedRequest()!, 'REJECTED')"
                class="px-6 py-3 rounded-xl bg-red-50 text-red-600 font-bold hover:bg-red-100 transition-colors">
                Reject
              </button>
              <button (click)="openApproveReject(selectedRequest()!, 'APPROVED')"
                class="px-6 py-3 rounded-xl bg-green-600 text-white font-bold hover:bg-green-700 transition-colors shadow-md">
                Approve
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Approve/Reject Confirmation Modal -->
      <div *ngIf="showApproveRejectModal()" class="fixed inset-0 z-[60] flex items-center justify-center p-4" (click)="closeApproveRejectModal()">
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm"></div>
        <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full relative z-10" (click)="$event.stopPropagation()">
          <div class="p-8">
            <h2 class="text-xl font-black text-slate-900 mb-2">
              {{ actionType() === 'APPROVED' ? 'Approve' : 'Reject' }} Request
            </h2>
            <p class="text-slate-500 text-sm mb-6">
              {{ actionType() === 'APPROVED'
                ? 'Are you sure you want to approve this visit request? The visitor will be notified via email.'
                : 'Are you sure you want to reject this visit request? Please provide a reason.' }}
            </p>

            <div class="mb-6">
              <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">
                Remarks {{ actionType() === 'REJECTED' ? '(required)' : '(optional)' }}
              </label>
              <textarea [(ngModel)]="remarks"
                rows="3"
                class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all resize-none"
                [placeholder]="actionType() === 'APPROVED' ? 'Add any notes...' : 'Reason for rejection...'">
              </textarea>
            </div>

            <div class="flex gap-3 justify-end">
              <button (click)="closeApproveRejectModal()"
                class="px-5 py-2.5 rounded-xl bg-slate-100 text-slate-700 font-bold hover:bg-slate-200 transition-colors">
                Cancel
              </button>
              <button (click)="submitApproveReject()"
                [disabled]="processing() || (actionType() === 'REJECTED' && !remarks.trim())"
                [class]="actionType() === 'APPROVED'
                  ? 'px-5 py-2.5 rounded-xl bg-green-600 text-white font-bold hover:bg-green-700 transition-colors shadow-md disabled:opacity-50 disabled:cursor-not-allowed'
                  : 'px-5 py-2.5 rounded-xl bg-red-600 text-white font-bold hover:bg-red-700 transition-colors shadow-md disabled:opacity-50 disabled:cursor-not-allowed'">
                <span *ngIf="processing()" class="flex items-center gap-2">
                  <span class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
                  Processing...
                </span>
                <span *ngIf="!processing()">
                  {{ actionType() === 'APPROVED' ? 'Confirm Approval' : 'Confirm Rejection' }}
                </span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Toast Notification -->
      <div *ngIf="toast()" 
        class="fixed bottom-6 right-6 z-[70] px-6 py-4 rounded-2xl shadow-xl font-bold text-sm animate-in slide-in-from-bottom duration-300"
        [class]="toast()!.type === 'success' ? 'bg-green-600 text-white' : 'bg-red-600 text-white'">
        {{ toast()!.message }}
      </div>
    </div>
  `,
  styles: [`
    .animate-in {
      animation: slideUp 0.3s ease-out;
    }
    @keyframes slideUp {
      from { opacity: 0; transform: translateY(1rem); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class AdminVisitRequestsComponent implements OnInit {
  allRequests = signal<VisitRequestResponse[]>([]);
  activeFilter = signal<string>('ALL');
  loading = signal(true);
  selectedRequest = signal<VisitRequestResponse | null>(null);
  showApproveRejectModal = signal(false);
  actionType = signal<'APPROVED' | 'REJECTED'>('APPROVED');
  actionTarget = signal<VisitRequestResponse | null>(null);
  processing = signal(false);
  toast = signal<{ message: string; type: 'success' | 'error' } | null>(null);
  remarks = '';

  statusTabs = [
    { label: 'All', value: 'ALL', activeClass: 'bg-slate-900 text-white' },
    { label: 'Pending', value: 'PENDING', activeClass: 'bg-amber-500 text-white' },
    { label: 'Approved', value: 'APPROVED', activeClass: 'bg-green-600 text-white' },
    { label: 'Rejected', value: 'REJECTED', activeClass: 'bg-red-600 text-white' },
    { label: 'Cancelled', value: 'CANCELLED', activeClass: 'bg-slate-500 text-white' },
    { label: 'Completed', value: 'COMPLETED', activeClass: 'bg-primary-600 text-white' },
  ];

  filteredRequests = computed(() => {
    const filter = this.activeFilter();
    const all = this.allRequests();
    const filtered = filter === 'ALL' ? all : all.filter(r => r.status === filter);
    return [...filtered].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  });

  constructor(private visitRequestService: VisitRequestService) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.loading.set(true);
    this.visitRequestService.getAllRequests().subscribe({
      next: (res) => {
        this.allRequests.set(res.data || []);
        this.loading.set(false);
      },
      error: () => {
        this.showToast('Failed to load visit requests', 'error');
        this.loading.set(false);
      }
    });
  }

  getCountForStatus(status: string): number {
    if (status === 'ALL') return this.allRequests().length;
    return this.allRequests().filter(r => r.status === status).length;
  }

  getStatusBadgeClass(status: VisitStatus | string): string {
    const base = 'px-3 py-1 rounded-full text-xs font-bold';
    switch (status) {
      case 'PENDING': return `${base} bg-amber-100 text-amber-700`;
      case 'APPROVED': return `${base} bg-green-100 text-green-700`;
      case 'REJECTED': return `${base} bg-red-100 text-red-700`;
      case 'CANCELLED': return `${base} bg-slate-100 text-slate-600`;
      case 'COMPLETED': return `${base} bg-primary-100 text-primary-700`;
      default: return `${base} bg-slate-100 text-slate-600`;
    }
  }

  openDetail(req: VisitRequestResponse): void {
    this.selectedRequest.set(req);
  }

  closeModals(): void {
    this.selectedRequest.set(null);
  }

  openApproveReject(req: VisitRequestResponse, action: 'APPROVED' | 'REJECTED'): void {
    this.actionTarget.set(req);
    this.actionType.set(action);
    this.remarks = '';
    this.showApproveRejectModal.set(true);
  }

  closeApproveRejectModal(): void {
    this.showApproveRejectModal.set(false);
    this.actionTarget.set(null);
    this.remarks = '';
  }

  submitApproveReject(): void {
    const target = this.actionTarget();
    if (!target) return;

    if (this.actionType() === 'REJECTED' && !this.remarks.trim()) return;

    this.processing.set(true);
    const request: ApproveRejectRequest = {
      status: this.actionType() as VisitStatus,
      remarks: this.remarks.trim() || undefined
    };

    this.visitRequestService.approveReject(target.id, request).subscribe({
      next: () => {
        const action = this.actionType() === 'APPROVED' ? 'approved' : 'rejected';
        this.showToast(`Visit request #${target.id} ${action} successfully`, 'success');
        this.closeApproveRejectModal();
        this.selectedRequest.set(null);
        this.processing.set(false);
        this.loadRequests();
      },
      error: (err) => {
        this.showToast(err.error?.message || 'Failed to process request', 'error');
        this.processing.set(false);
      }
    });
  }

  showToast(message: string, type: 'success' | 'error'): void {
    this.toast.set({ message, type });
    setTimeout(() => this.toast.set(null), 4000);
  }
}
