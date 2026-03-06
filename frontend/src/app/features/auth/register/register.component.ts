import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { Role } from '../../../core/models/vms.models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-50 p-4 py-12">
      <div class="max-w-2xl w-full glass p-8 rounded-3xl shadow-2xl relative overflow-hidden">
        <div class="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-primary-600 to-cyan-500"></div>
        
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-primary-600 rounded-2xl flex items-center justify-center text-white font-bold text-3xl mx-auto mb-4 shadow-lg rotate-3">
            V
          </div>
          <h2 class="text-3xl font-black text-slate-900">Create Account</h2>
          <p class="text-slate-500 mt-2">Join VMS Intellect for smart visitor tracking</p>
        </div>

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="md:col-span-2">
             <label class="block text-sm font-bold text-slate-700 mb-2">Full Name</label>
             <input type="text" formControlName="name"
               class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
               placeholder="John Doe">
          </div>

          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2">Email Address</label>
            <input type="email" formControlName="email"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
              placeholder="name@company.com">
          </div>

          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2">Mobile Number</label>
            <input type="text" formControlName="mobileNumber"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
              placeholder="+91 9876543210">
          </div>

          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2">Password</label>
            <input type="password" formControlName="password"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
              placeholder="••••••••">
          </div>

          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2">Confirm Password</label>
            <input type="password" formControlName="confirmPassword"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
              placeholder="••••••••">
          </div>

          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2">Role</label>
            <select formControlName="role"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all appearance-none bg-white">
              <option value="ADMIN">Admin</option>
              <option value="ASSOCIATE">Associate</option>
              <option value="SECURITY">Security Guard</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2">Department</label>
            <input type="text" formControlName="department"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
              placeholder="Engineering">
          </div>

          <div class="md:col-span-2 text-red-500 text-sm" *ngIf="error">{{ error }}</div>

          <button type="submit" [disabled]="registerForm.invalid || isLoading"
            class="md:col-span-2 py-4 bg-primary-600 text-white rounded-xl font-bold shadow-lg hover:bg-primary-700 hover:shadow-xl transition-all disabled:opacity-50 active:scale-95 flex items-center justify-center gap-2">
            <span *ngIf="isLoading" class="animate-spin border-2 border-white border-t-transparent rounded-full w-5 h-5"></span>
            <span>Create Account</span>
          </button>
        </form>

        <div class="mt-8 text-center">
          <p class="text-slate-500">Already have an account? 
            <a routerLink="/login" class="text-primary-600 font-bold hover:underline">Sign In</a>
          </p>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  registerForm: FormGroup;
  isLoading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    const defaultRole = this.route.snapshot.queryParamMap.get('role') || 'ASSOCIATE';
    
    this.registerForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      mobileNumber: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      role: [defaultRole, [Validators.required]],
      department: [''],
      designation: ['']
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('password')?.value === g.get('confirmPassword')?.value
      ? null : { 'mismatch': true };
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      const { confirmPassword, ...registerData } = this.registerForm.value;
      this.authService.register(registerData).subscribe({
        next: (res) => {
          this.isLoading = false;
          const role = res.data.role;
          if (role === Role.ADMIN) this.router.navigate(['/admin']);
          else if (role === Role.ASSOCIATE) this.router.navigate(['/associate']);
          else if (role === Role.SECURITY) this.router.navigate(['/security']);
          else this.router.navigate(['/']);
        },
        error: (err) => {
          this.isLoading = false;
          this.error = 'Registration failed. Email might already exist.';
        }
      });
    }
  }
}
