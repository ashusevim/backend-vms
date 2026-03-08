## 🎨 Member 8 — Shared UI, Routing, Static Pages & DevOps

**Owner:** _[Name]_

### Files
| Layer | Files |
|-------|-------|
| Shared | `navbar.component.ts` |
| Static | `home.component.ts`, `contact.component.ts` |
| Routing | `app.routes.ts` |
| Config | `tailwind.config.js`, `environment.ts`, `environment.prod.ts` |

### Code Walkthrough — Routing

#### app.routes.ts — Application Routing
```typescript
export const routes: Routes = [
    // Public routes — accessible without login
    { path: '', component: HomeComponent },                    // Landing page
    { path: 'login', component: LoginComponent },              // Login form
    { path: 'register', component: RegisterComponent },        // Registration form
    { path: 'contact', component: ContactComponent },          // Contact page

    // Protected routes — require authentication + specific role
    {
        path: 'admin/dashboard',
        component: AdminDashboardComponent,
        canActivate: [AuthGuard],                              // Must be logged in
        data: { role: 'ADMIN' }                                // Must have ADMIN role
        // AuthGuard reads this 'data.role' and checks against JWT role
    },
    {
        path: 'admin/visit-requests',
        component: AdminVisitRequestsComponent,
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
    },
    {
        path: 'associate/dashboard',
        component: AssociateDashboardComponent,
        canActivate: [AuthGuard],
        data: { role: 'ASSOCIATE' }
    },
    {
        path: 'security/dashboard',
        component: SecurityDashboardComponent,
        canActivate: [AuthGuard],
        data: { role: 'SECURITY' }
    },

    // Wildcard — any unknown URL redirects to home
    { path: '**', redirectTo: '' }
    // Example: /xyz → redirects to / (home page)
];
```

### Code Walkthrough — Navbar

#### navbar.component.ts — Role-Based Navigation
```typescript
export class NavbarComponent {
    authService = inject(AuthService);

    // What it does: Checks if user is logged in to show/hide nav items
    isLoggedIn(): boolean {
        return this.authService.isLoggedIn();
    }

    // What it does: Gets current user's role to show role-specific menu items
    getUserRole(): string {
        return localStorage.getItem('role') || '';
    }

    getUserName(): string {
        return localStorage.getItem('userName') || 'User';
    }

    logout(): void {
        this.authService.logout();
    }
}

// In template — conditional navigation based on role:
// <nav>
//   <a routerLink="/">Home</a>
//   <a routerLink="/contact">Contact</a>
//
//   <!-- Only shown when logged in as ADMIN -->
//   @if (getUserRole() === 'ADMIN') {
//     <a routerLink="/admin/dashboard">Dashboard</a>
//     <a routerLink="/admin/visit-requests">Visit Requests</a>
//   }
//
//   <!-- Only shown when logged in as ASSOCIATE -->
//   @if (getUserRole() === 'ASSOCIATE') {
//     <a routerLink="/associate/dashboard">My Dashboard</a>
//   }
//
//   <!-- Only shown when logged in as SECURITY -->
//   @if (getUserRole() === 'SECURITY') {
//     <a routerLink="/security/dashboard">Scanner</a>
//   }
//
//   @if (isLoggedIn()) {
//     <span>Welcome, {{ getUserName() }}</span>
//     <button (click)="logout()">Logout</button>
//   } @else {
//     <a routerLink="/login">Login</a>
//   }
// </nav>
```

### Code Walkthrough — Landing Page

#### home.component.ts — Animations & Hero Section
```typescript
@Component({
    // ...
    animations: [
        trigger('fadeInUp', [
            transition(':enter', [
                // What it does: Elements start invisible and below their final position
                style({ opacity: 0, transform: 'translateY(30px)' }),
                // Then animate to visible and normal position over 600ms
                animate('600ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
            ])
        ]),
        trigger('staggerFadeIn', [
            transition(':enter', [
                query(':enter', [
                    style({ opacity: 0, transform: 'translateY(20px)' }),
                    // What it does: Staggers child element animations by 100ms each
                    stagger(100, [
                        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
                    ])
                    // Result: Feature cards appear one by one, not all at once
                ], { optional: true })
            ])
        ])
    ]
})
```

```html
<!-- Hero Section Template -->
<section class="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 to-white">
    <div @fadeInUp>
        <!-- Gradient text effect -->
        <h1 class="text-6xl font-black bg-gradient-to-r from-primary-600 to-primary-400 
                    bg-clip-text text-transparent">
            VMS Intellect
        </h1>
        <!--  bg-gradient-to-r = gradient goes left to right
              bg-clip-text = clips gradient to text shape
              text-transparent = makes text color transparent so gradient shows through
              Result: Text has a gradient color effect -->

        <p class="text-xl text-gray-600 mt-4">Intelligent Visitor Management System</p>

        <!-- CTA Buttons — each registers with a different role -->
        <div class="flex gap-4 mt-8">
            <a routerLink="/register" [queryParams]="{role: 'ADMIN'}"
               class="bg-primary-600 text-white px-8 py-3 rounded-full hover:bg-primary-700 
                      transition-all duration-300 shadow-lg hover:shadow-xl">
                Register as Admin
            </a>
            <a routerLink="/register" [queryParams]="{role: 'ASSOCIATE'}"
               class="border-2 border-primary-600 text-primary-600 px-8 py-3 rounded-full
                      hover:bg-primary-600 hover:text-white transition-all duration-300">
                Register as Associate
            </a>
            <a routerLink="/register" [queryParams]="{role: 'SECURITY'}"
               class="border-2 border-primary-600 text-primary-600 px-8 py-3 rounded-full
                      hover:bg-primary-600 hover:text-white transition-all duration-300">
                Register as Security
            </a>
        </div>
    </div>
</section>
```

### Code Walkthrough — Contact Page

#### contact.component.ts — Glass Morphism Cards
```html
<!-- Glass morphism card effect -->
<div class="backdrop-blur-lg bg-white/30 rounded-3xl p-8 shadow-xl 
            border border-white/20 hover:bg-white/40 transition-all duration-300">
    <!--  backdrop-blur-lg = blurs content behind the card (frosted glass effect)
          bg-white/30 = white background at 30% opacity
          border-white/20 = white border at 20% opacity
          hover:bg-white/40 = slightly more opaque on hover
          Result: semi-transparent card with blur effect -->

    <div class="text-4xl mb-4">📧</div>
    <h3 class="text-xl font-bold text-gray-800">Email Us</h3>
    <p class="text-gray-600 mt-2">support@vmsintellect.com</p>
</div>
```

### Code Walkthrough — Tailwind Configuration

#### tailwind.config.js
```javascript
module.exports = {
    content: ['./src/**/*.{html,ts}'],  // Scans all HTML and TS files for class names
    //        ^^^^^^^^^^^^^^^^^^^^^^^^
    //        Tailwind tree-shakes unused classes — only includes classes found in these files
    //        This keeps the CSS bundle small (~20KB vs ~3MB full Tailwind)

    theme: {
        extend: {
            colors: {
                primary: {
                    50:  '#f0f9ff',   // Lightest — used for backgrounds
                    100: '#e0f2fe',
                    200: '#bae6fd',
                    300: '#7dd3fc',
                    400: '#38bdf8',
                    500: '#0ea5e9',   // Base — main brand color (sky blue)
                    600: '#0284c7',   // Primary buttons, headings
                    700: '#0369a1',   // Hover states
                    800: '#075985',
                    900: '#0c4a6e',   // Darkest — used sparingly
                }
                // Usage: class="bg-primary-600 text-primary-50 hover:bg-primary-700"
                // This creates a consistent brand color system across the entire app
            }
        }
    },
    plugins: []
};
```

### Code Walkthrough — Environment Configuration

#### environment.ts (Development)
```typescript
export const environment = {
    production: false,
    apiUrl: 'http://localhost:8080/api'
    //      ^^^^^^^^^^^^^^^^^^^^^^^^
    //      During development, Angular dev server runs on :4200
    //      Spring Boot runs on :8080
    //      All HTTP calls go to localhost:8080/api/*
};
```

#### environment.prod.ts (Production)
```typescript
export const environment = {
    production: true,
    apiUrl: '/api'
    //      ^^^^
    //      In production, frontend and backend are served from the SAME domain
    //      So relative URL '/api' works (no CORS issues)
    //      Example: https://vmsintellect.com/api/auth/login
};
```

### Demo Script
1. Open `/` (home page) → Show the hero section with gradient text and animations
2. Show feature cards appearing with stagger animation
3. Show the CTA buttons → Click one → Show `?role=ADMIN` in URL
4. Navigate to `/contact` → Show glass morphism card effects (move background to see blur)
5. Login as different roles → Show navbar changing (different menu items for each role)
6. Open `app.routes.ts` → Walk through the route definitions
7. Open `tailwind.config.js` → Show custom color palette
8. Open DevTools → Show CSS classes → Explain Tailwind utility-first approach
9. Try navigating to `/admin/dashboard` as ASSOCIATE → Show redirect (AuthGuard)

### Questions They Might Ask
- **Q: What is Tailwind CSS?** → A: Utility-first CSS framework — instead of writing CSS classes, you compose styles with small utility classes directly in HTML
- **Q: Why not use Bootstrap?** → A: Tailwind is more customizable, smaller bundle size (tree-shaking), no pre-designed components to override
- **Q: What is glass morphism?** → A: Design trend using semi-transparent elements with blur — `backdrop-blur` + low `opacity` background
- **Q: How does `canActivate` guard work?** → A: It runs before navigation — checks if token exists and role matches `data.role`; if not, redirects to `/login`
- **Q: Why two environment files?** → A: Angular CLI replaces `environment.ts` with `environment.prod.ts` during `ng build --prod` — different API URLs for dev/prod
- **Q: What does `@fadeInUp` do in the template?** → A: The `@` prefix triggers Angular's animation system — binds the `fadeInUp` animation trigger to the element's enter/leave lifecycle

---
