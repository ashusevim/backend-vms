import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { Role } from '../../../core/models/vms.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <div class="max-w-md w-full glass p-8 rounded-3xl shadow-2xl relative overflow-hidden">
        <div class="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-primary-600 to-cyan-500"></div>
        
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-primary-600 rounded-2xl flex items-center justify-center text-white font-bold text-3xl mx-auto mb-4 shadow-lg rotate-3">
            V
          </div>
          <h2 class="text-3xl font-black text-slate-900">Welcome Back</h2>
          <p class="text-slate-500 mt-2">Login to your VMS Intellect account</p>
        </div>

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-6">
          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2">Email Address</label>
            <input type="email" formControlName="email"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
              placeholder="name@company.com">
          </div>

          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2">Password</label>
            <input type="password" formControlName="password"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
              placeholder="••••••••">
          </div>

          <div class="text-red-500 text-sm" *ngIf="error">{{ error }}</div>

          <button type="submit" [disabled]="loginForm.invalid || isLoading"
            class="w-full py-4 bg-primary-600 text-white rounded-xl font-bold shadow-lg hover:bg-primary-700 hover:shadow-xl transition-all disabled:opacity-50 active:scale-95 flex items-center justify-center gap-2">
            <span *ngIf="isLoading" class="animate-spin border-2 border-white border-t-transparent rounded-full w-5 h-5"></span>
            <span>Sign In</span>
          </button>
        </form>

        <div class="mt-8 text-center">
          <p class="text-slate-500">Don't have an account? 
            <a routerLink="/register" class="text-primary-600 font-bold hover:underline">Register</a>
          </p>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });

    // Redirect already-logged-in users to their dashboard
    if (this.authService.isLoggedIn()) {
      this.redirectByRole(this.authService.currentUser()!.role);
    }
  }

  private redirectByRole(role: Role): void {
    if (role === Role.ADMIN) this.router.navigate(['/admin']);
    else if (role === Role.ASSOCIATE) this.router.navigate(['/associate']);
    else if (role === Role.SECURITY) this.router.navigate(['/security']);
    else this.router.navigate(['/']);
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.authService.login(this.loginForm.value).subscribe({
        next: (res) => {
          this.isLoading = false;
          this.redirectByRole(res.data.role);
        },
        error: (err) => {
          this.isLoading = false;
          this.error = 'Invalid email or password';
        }
      });
    }
  }
}
