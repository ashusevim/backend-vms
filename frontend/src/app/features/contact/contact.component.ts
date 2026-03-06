import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-slate-50 to-primary-50 pt-28 pb-16 px-4">
      <div class="max-w-3xl mx-auto">
        <h1 class="text-4xl font-black text-center mb-2 bg-clip-text text-transparent bg-gradient-to-r from-primary-700 to-primary-500">
          Contact Us
        </h1>
        <p class="text-center text-slate-500 mb-12">Have questions? Reach out to us through any of the channels below.</p>

        <div class="grid gap-8 md:grid-cols-2">
          <!-- Phone -->
          <div class="glass rounded-2xl p-8 flex flex-col items-center text-center shadow-lg hover:shadow-xl transition-shadow">
            <div class="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mb-4">
              <svg class="w-8 h-8 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
            </div>
            <h2 class="text-xl font-bold text-slate-800 mb-2">Phone</h2>
            <a href="tel:+11234567890" class="text-primary-600 hover:text-primary-700 text-lg font-medium transition-colors">
              +1 (123) 456-7890
            </a>
            <p class="text-slate-400 text-sm mt-2">Mon – Fri, 9 AM – 6 PM</p>
          </div>

          <!-- Email -->
          <div class="glass rounded-2xl p-8 flex flex-col items-center text-center shadow-lg hover:shadow-xl transition-shadow">
            <div class="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mb-4">
              <svg class="w-8 h-8 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
            </div>
            <h2 class="text-xl font-bold text-slate-800 mb-2">Email</h2>
            <a href="mailto:support@vmsintellect.com" class="text-primary-600 hover:text-primary-700 text-lg font-medium transition-colors">
              support&#64;vmsintellect.com
            </a>
            <p class="text-slate-400 text-sm mt-2">We reply within 24 hours</p>
          </div>
        </div>

        <!-- Address / Extra Info -->
        <div class="glass rounded-2xl p-8 mt-8 text-center shadow-lg">
          <h2 class="text-xl font-bold text-slate-800 mb-2">Office Address</h2>
          <p class="text-slate-600">123 Innovation Drive, Suite 400<br>Tech City, TX 75001</p>
        </div>

        <div class="text-center mt-10">
          <a routerLink="/" class="text-primary-600 hover:text-primary-700 font-medium transition-colors">
            &larr; Back to Home
          </a>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class ContactComponent {}
