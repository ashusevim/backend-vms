import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { VisitorService, Visitor } from '../../core/services/visitor.service';
import { VisitRequestService } from '../../core/services/visit-request.service';
import { AuthService } from '../../core/services/auth.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

@Component({
  selector: 'app-associate-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NavbarComponent],
  template: `
    <div class="min-h-screen bg-slate-50">
      <app-navbar></app-navbar>

      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-12">
        <div class="grid lg:grid-cols-3 gap-8">
          
          <!-- Left Column: Request Form -->
          <div class="lg:col-span-2 space-y-8">
            <header>
              <h1 class="text-3xl font-black text-slate-900">Schedule a Visit</h1>
              <p class="text-slate-500">Search for an existing visitor or register a new one</p>
            </header>

            <div class="bg-white p-8 rounded-[2rem] shadow-sm border border-slate-100">
              <!-- Step 1: Search -->
              <div class="mb-8">
                <label class="block text-sm font-bold text-slate-700 mb-2">Search Visitor (Email or Mobile)</label>
                <div class="flex gap-2">
                  <div class="relative flex-1">
                    <input type="text" [formControl]="searchControl" (keydown.enter)="onSearch()"
                      class="w-full px-4 py-4 pl-12 rounded-2xl border border-slate-200 focus:border-primary-500 focus:ring-4 focus:ring-primary-100 outline-none transition-all"
                      placeholder="Search by email or phone...">
                    <svg class="w-6 h-6 text-slate-400 absolute left-4 top-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                    </svg>
                  </div>
                  <button type="button" (click)="onSearch()"
                    class="px-6 py-4 bg-primary-600 text-white rounded-2xl font-bold shadow-md hover:bg-primary-700 hover:shadow-lg transition-all active:scale-95 flex items-center gap-2">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                    </svg>
                    Search
                  </button>
                </div>
                <p *ngIf="searchNoResults()" class="mt-3 text-sm text-slate-500 italic">No visitors found. Fill in the details below to register a new visitor.</p>

                <!-- Search Results -->
                <div *ngIf="searchResults().length > 0" class="mt-4 space-y-2">
                  <div *ngFor="let visitor of searchResults()" 
                    (click)="selectVisitor(visitor)"
                    class="p-4 border border-slate-100 rounded-xl hover:bg-primary-50 hover:border-primary-200 cursor-pointer transition-all flex justify-between items-center">
                    <div>
                      <p class="font-bold text-slate-900">{{ visitor.name }}</p>
                      <p class="text-sm text-slate-500">{{ visitor.email }} | {{ visitor.mobileNumber }}</p>
                    </div>
                    <span class="text-primary-600 font-bold text-sm">Select &rarr;</span>
                  </div>
                </div>
              </div>

              <!-- Step 2: Form (Conditional) -->
              <form [formGroup]="visitForm" (ngSubmit)="onSubmit()" class="space-y-6">
                
                <!-- Visitor Info (Prefilled if selected, editable if new) -->
                <div class="grid md:grid-cols-2 gap-6 p-6 bg-slate-50 rounded-2xl border border-slate-100">
                  <div class="md:col-span-2 flex justify-between items-center px-2">
                    <h3 class="font-bold text-slate-900">Visitor Details</h3>
                    <button type="button" *ngIf="selectedVisitor()" (click)="resetVisitor()" class="text-xs text-red-500 font-bold hover:underline">Change Visitor</button>
                  </div>
                  
                  <div formGroupName="visitor">
                    <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Name <span class="text-red-500">*</span></label>
                    <input type="text" formControlName="name" [readonly]="!!selectedVisitor()"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 transition-all" [class.bg-slate-100]="selectedVisitor()">
                  </div>
                  <div formGroupName="visitor">
                    <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Email <span class="text-red-500">*</span></label>
                    <input type="email" formControlName="email" [readonly]="!!selectedVisitor()"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 transition-all" [class.bg-slate-100]="selectedVisitor()">
                  </div>
                  <div formGroupName="visitor">
                    <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Mobile <span class="text-red-500">*</span></label>
                    <input type="text" formControlName="mobileNumber" [readonly]="!!selectedVisitor()"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 transition-all" [class.bg-slate-100]="selectedVisitor()">
                  </div>
                  <div formGroupName="visitor">
                    <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">ID Proof Type</label>
                    <select formControlName="idProofType" [attr.disabled]="selectedVisitor() ? true : null"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 transition-all appearance-none bg-white font-medium" [class.bg-slate-100]="selectedVisitor()">
                      <option value="AADHAAR">Aadhaar Card</option>
                      <option value="PAN">PAN Card</option>
                      <option value="DRIVING_LICENSE">Driving License</option>
                      <option value="PASSPORT">Passport</option>
                    </select>
                  </div>
                  <div formGroupName="visitor" class="md:col-span-2">
                    <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Company Name</label>
                    <input type="text" formControlName="companyName" [readonly]="!!selectedVisitor()"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 transition-all" [class.bg-slate-100]="selectedVisitor()">
                  </div>
                  <div formGroupName="visitor" class="md:col-span-2">
                    <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">ID Proof Number <span class="text-red-500">*</span></label>
                    <input type="text" formControlName="idProofNumber" [readonly]="!!selectedVisitor()"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 transition-all" [class.bg-slate-100]="selectedVisitor()">
                  </div>

                  <!-- Photo Upload -->
                  <div class="md:col-span-2">
                    <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Visitor Photo <span class="text-red-500">*</span></label>
                    <div class="flex items-center gap-4">
                      <div class="w-20 h-20 rounded-xl border-2 border-dashed border-slate-200 flex items-center justify-center overflow-hidden bg-slate-50">
                        <img *ngIf="photoPreview()" [src]="photoPreview()" class="w-full h-full object-cover" />
                        <svg *ngIf="!photoPreview()" class="w-8 h-8 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
                      </div>
                      <div>
                        <label class="px-4 py-2 bg-primary-50 text-primary-600 rounded-lg text-sm font-bold cursor-pointer hover:bg-primary-100 transition-colors inline-block">
                          {{ photoPreview() ? 'Change Photo' : 'Upload Photo' }}
                          <input type="file" accept="image/*" class="hidden" (change)="onPhotoSelected($event)" />
                        </label>
                        <p class="text-xs text-slate-400 mt-1">JPG, PNG (max 5MB). Used for the access card.</p>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Visit Details -->
                <div class="grid md:grid-cols-2 gap-6">
                  <div class="md:col-span-2">
                    <label class="block text-sm font-bold text-slate-700 mb-2">Purpose of Visit <span class="text-red-500">*</span></label>
                    <textarea formControlName="purpose" rows="2"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
                      placeholder="e.g. Project discussion, Interview..."></textarea>
                  </div>
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">From Date</label>
                    <input type="date" formControlName="fromDate"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all">
                  </div>
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">To Date</label>
                    <input type="date" formControlName="toDate"
                      class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all">
                  </div>
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">In Time</label>
                    <input type="time" formControlName="inTime"
                      class="w-full px-4 py-4 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all">
                  </div>
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">Out Time</label>
                    <input type="time" formControlName="outTime"
                      class="w-full px-4 py-4 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all">
                  </div>
                </div>

                <button type="submit" [disabled]="visitForm.invalid || isLoading"
                  class="w-full py-4 bg-slate-900 text-white rounded-2xl font-black shadow-xl hover:bg-slate-800 transition-all active:scale-95 disabled:opacity-50 flex items-center justify-center gap-2">
                  <span *ngIf="isLoading" class="animate-spin border-2 border-white border-t-transparent rounded-full w-5 h-5"></span>
                  <span>Create Visit Request</span>
                </button>
              </form>
            </div>
          </div>

          <!-- Right Column: My Requests -->
          <div class="space-y-6">
            <header>
              <h2 class="text-2xl font-black text-slate-900">Recent Requests</h2>
              <p class="text-sm text-slate-500">History of your invitations</p>
            </header>

            <div class="space-y-4">
              <div *ngFor="let req of myRequests()" 
                class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 hover:shadow-md transition-all group">
                <div class="flex justify-between items-start mb-4">
                  <div>
                    <p class="font-bold text-slate-900">{{ req.visitorName }}</p>
                    <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">{{ req.fromDate | date:'mediumDate' }}</p>
                  </div>
                  <span [ngClass]="{
                    'bg-amber-100 text-amber-600': req.status === 'PENDING',
                    'bg-green-100 text-green-600': req.status === 'APPROVED',
                    'bg-red-100 text-red-600': req.status === 'REJECTED'
                  }" class="px-3 py-1 rounded-full text-[10px] font-black tracking-widest uppercase">
                    {{ req.status }}
                  </span>
                </div>
                <p class="text-sm text-slate-600 mb-4 line-clamp-2 italic">"{{ req.purpose }}"</p>
                <div class="flex justify-end gap-2" *ngIf="req.status === 'PENDING'">
                  <button (click)="cancelRequest(req.id)" class="text-xs font-bold text-red-500 hover:text-red-700 transition-colors uppercase tracking-widest">Cancel</button>
                </div>
              </div>
            </div>
          </div>

        </div>
      </main>
      <!-- Toast Notification -->
      <div *ngIf="toast()" 
        class="fixed bottom-6 right-6 z-50 px-6 py-4 rounded-2xl shadow-2xl text-white font-bold flex items-center gap-3 animate-in slide-in-from-bottom duration-300"
        [ngClass]="{'bg-green-500': toast()!.type === 'success', 'bg-red-500': toast()!.type === 'error'}">
        <svg *ngIf="toast()!.type === 'success'" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
        <svg *ngIf="toast()!.type === 'error'" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
        {{ toast()!.message }}
      </div>
    </div>
  `
})
export class AssociateDashboardComponent implements OnInit {
  visitForm: FormGroup;
  searchControl = this.fb.control('');
  searchResults = signal<Visitor[]>([]);
  searchNoResults = signal<boolean>(false);
  selectedVisitor = signal<Visitor | null>(null);
  myRequests = signal<any[]>([]);
  photoPreview = signal<string | null>(null);
  selectedPhotoFile = signal<File | null>(null);
  toast = signal<{message: string, type: 'success' | 'error'} | null>(null);
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private visitorService: VisitorService,
    private visitRequestService: VisitRequestService,
    private authService: AuthService
  ) {
    this.visitForm = this.fb.group({
      visitor: this.fb.group({
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        mobileNumber: ['', Validators.required],
        companyName: [''],
        idProofType: ['AADHAAR', Validators.required],
        idProofNumber: ['', Validators.required]
      }),
      purpose: ['', Validators.required],
      fromDate: ['', Validators.required],
      toDate: ['', Validators.required],
      inTime: ['', Validators.required],
      outTime: ['', Validators.required]
    });

    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => query && query.length > 2 ? this.visitorService.searchVisitors(query) : [])
    ).subscribe(res => {
      const data = (res as any).data || [];
      this.searchResults.set(data);
      this.searchNoResults.set(data.length === 0 && !!this.searchControl.value && this.searchControl.value.length > 2);
    });
  }

  onSearch() {
    const query = this.searchControl.value?.trim();
    if (query && query.length > 0) {
      this.visitorService.searchVisitors(query).subscribe({
        next: (res) => {
          const data = res.data || [];
          this.searchResults.set(data);
          this.searchNoResults.set(data.length === 0);
        },
        error: () => {
          this.searchResults.set([]);
          this.searchNoResults.set(true);
        }
      });
    }
  }

  ngOnInit(): void {
    this.loadMyRequests();
  }

  loadMyRequests() {
    const user = this.authService.currentUser();
    if (user) {
      this.visitRequestService.getAssociateRequests(user.id).subscribe(res => {
        this.myRequests.set(res.data);
      });
    }
  }

  selectVisitor(visitor: Visitor) {
    this.selectedVisitor.set(visitor);
    this.visitForm.get('visitor')?.patchValue(visitor);
    this.searchResults.set([]);
    this.searchControl.setValue('');
    this.photoPreview.set(null);
    this.selectedPhotoFile.set(null);
  }

  resetVisitor() {
    this.selectedVisitor.set(null);
    this.visitForm.get('visitor')?.reset({ idProofType: 'AADHAAR' });
    this.photoPreview.set(null);
    this.selectedPhotoFile.set(null);
  }

  onPhotoSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      if (file.size > 5 * 1024 * 1024) {
        alert('Photo must be less than 5MB');
        return;
      }
      this.selectedPhotoFile.set(file);
      const reader = new FileReader();
      reader.onload = (e) => this.photoPreview.set(e.target?.result as string);
      reader.readAsDataURL(file);
    }
  }

  onSubmit() {
    if (this.visitForm.valid) {
      this.isLoading = true;
      const user = this.authService.currentUser();
      const formValue = this.visitForm.value;
      
      const payload: any = {
        associateId: user?.id,
        purpose: formValue.purpose,
        fromDate: formValue.fromDate,
        toDate: formValue.toDate,
        inTime: formValue.inTime,
        outTime: formValue.outTime,
        category: 'BUSINESS' // Default category
      };

      if (this.selectedVisitor()) {
        payload.visitorId = this.selectedVisitor()?.id;
      } else {
        payload.visitorName = formValue.visitor.name;
        payload.visitorEmail = formValue.visitor.email;
        payload.visitorMobile = formValue.visitor.mobileNumber;
        payload.visitorCompany = formValue.visitor.companyName;
        payload.visitorIdProofType = formValue.visitor.idProofType;
        payload.visitorIdProofNumber = formValue.visitor.idProofNumber;
      }

      this.visitRequestService.createRequest(payload).subscribe({
        next: (res) => {
          // Upload photo if one was selected
          const visitorId = this.selectedVisitor()?.id || res.data?.visitorId;
          if (this.selectedPhotoFile() && visitorId) {
            this.visitorService.uploadPhoto(visitorId, this.selectedPhotoFile()!).subscribe({
              next: () => {
                this.isLoading = false;
                this.showToast('Visit request created successfully!', 'success');
                this.visitForm.reset({ visitor: { idProofType: 'AADHAAR' } });
                this.selectedVisitor.set(null);
                this.photoPreview.set(null);
                this.selectedPhotoFile.set(null);
                this.loadMyRequests();
              },
              error: () => {
                // Request created but photo upload failed - still reset
                this.isLoading = false;
                this.showToast('Visit request created but photo upload failed.', 'error');
                this.visitForm.reset({ visitor: { idProofType: 'AADHAAR' } });
                this.selectedVisitor.set(null);
                this.photoPreview.set(null);
                this.selectedPhotoFile.set(null);
                this.loadMyRequests();
              }
            });
          } else {
            this.isLoading = false;
            this.showToast('Visit request created successfully!', 'success');
            this.visitForm.reset({ visitor: { idProofType: 'AADHAAR' } });
            this.selectedVisitor.set(null);
            this.photoPreview.set(null);
            this.selectedPhotoFile.set(null);
            this.loadMyRequests();
          }
        },
        error: () => {
          this.isLoading = false;
          this.showToast('Failed to create visit request. Please try again.', 'error');
        }
      });
    }
  }

  cancelRequest(id: number) {
    if (confirm('Are you sure you want to cancel this request?')) {
      this.visitRequestService.cancelRequest(id).subscribe(() => this.loadMyRequests());
    }
  }

  showToast(message: string, type: 'success' | 'error') {
    this.toast.set({ message, type });
    setTimeout(() => this.toast.set(null), 4000);
  }
}
