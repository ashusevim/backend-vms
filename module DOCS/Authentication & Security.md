## 🔐 Member 1 — Authentication & Security

**Owner:** _[Name]_

### Files
| Layer | Files |
|-------|-------|
| Backend | `JwtUtil.java`, `JwtAuthFilter.java`, `SecurityConfig.java`, `AuthController.java` |
| Frontend | `login.component.ts`, `register.component.ts`, `auth.service.ts` |
| Models | `AuthResponse` interface in `vms.models.ts` |

### Code Walkthrough — Backend

#### JwtUtil.java — Token Generation & Validation
```java
// What it does: Generates a signed JWT token with user details embedded as claims
public String generateToken(String username, String role) {
    return Jwts.builder()
        .setSubject(username)              // Sets the user email as the "subject" claim
        .claim("role", role)               // Embeds user role (ADMIN/ASSOCIATE/SECURITY) inside the token
        .setIssuedAt(new Date())           // Timestamp when token was created
        .setExpiration(new Date(System.currentTimeMillis() + expiration))  // Token expires after configured time
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Signs with HMAC-SHA256 using secret key
        .compact();                        // Builds and returns the token string
}

// What it does: Extracts the username (email) from a given token
public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);  // Parses the token and pulls the "subject" field
}

// What it does: Checks if token belongs to this user AND hasn't expired
public boolean isTokenValid(String token, String username) {
    return (extractUsername(token).equals(username) && !isTokenExpired(token));
}
```

#### JwtAuthFilter.java — Request Interception
```java
// What it does: Intercepts every HTTP request to check for valid JWT
@Override
protected void doFilterInternal(HttpServletRequest request, ...) {
    String authHeader = request.getHeader("Authorization");  // Gets "Authorization" header
    
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);  // No token → skip auth, let Spring Security handle
        return;
    }
    
    String token = authHeader.substring(7);                  // Removes "Bearer " prefix to get raw token
    String username = jwtUtil.extractUsername(token);         // Extracts email from token
    
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {
            // Creates authentication object and sets it in Spring's security context
            // → This is what makes @PreAuthorize and role checks work
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    filterChain.doFilter(request, response);  // Continue to the next filter/controller
}
```

### Code Walkthrough — Frontend

#### auth.service.ts — Login & Token Storage
```typescript
// What it does: Sends login credentials to backend and stores the JWT token
login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, { email, password })
        .pipe(
            tap(response => {
                localStorage.setItem('token', response.token);   // Stores JWT in browser storage
                localStorage.setItem('role', response.role);     // Stores role for route guarding
                localStorage.setItem('userName', response.name); // Stores display name
            })
        );
}

// What it does: Checks if user is currently logged in by verifying token exists
isLoggedIn(): boolean {
    return !!localStorage.getItem('token');  // Returns true if token exists in localStorage
}

// What it does: Removes all auth data — effectively logs user out
logout(): void {
    localStorage.clear();        // Clears all stored data
    this.router.navigate(['/']); // Redirects to home page
}
```

#### login.component.ts — Login Form & Routing
```typescript
// What it does: Handles form submission, calls auth service, redirects based on role
onSubmit(): void {
    if (this.loginForm.invalid) return;  // Stops if validation fails (empty fields, invalid email)
    
    const { email, password } = this.loginForm.value;
    this.authService.login(email, password).subscribe({
        next: (response) => {
            // Routes user to their role-specific dashboard after successful login
            switch (response.role) {
                case 'ADMIN':     this.router.navigate(['/admin/dashboard']); break;
                case 'ASSOCIATE': this.router.navigate(['/associate/dashboard']); break;
                case 'SECURITY':  this.router.navigate(['/security/dashboard']); break;
            }
        },
        error: (err) => {
            this.errorMessage = 'Invalid credentials';  // Shows error on the login form
        }
    });
}
```

#### register.component.ts — Role-Based Registration
```typescript
// What it does: Reads role from URL query param and registers user with that role
ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
        this.role = params['role'] || 'ASSOCIATE';  // Default to ASSOCIATE if no role specified
        // URL example: /register?role=ADMIN
    });
}

onSubmit(): void {
    const userData = { ...this.registerForm.value, role: this.role };
    // Sends: { name, email, password, mobileNumber, department, designation, role }
    this.authService.register(userData).subscribe({
        next: () => this.router.navigate(['/login']),  // Success → redirect to login
        error: (err) => this.errorMessage = err.error.message
    });
}
```

### Demo Script
1. Open `register?role=ADMIN` → Register an admin → Show data saved in DB
2. Open `/login` → Login with credentials → Show JWT in browser DevTools → `Application > localStorage`
3. Copy token → Go to [jwt.io](https://jwt.io) → Decode → Show claims (subject, role, expiry)
4. Try accessing `/admin/dashboard` without login → Show redirect to `/login` (guard in action)
5. Show `Network` tab → Observe `Authorization: Bearer <token>` header in API calls

### Questions They Might Ask
- **Q: Why JWT over sessions?** → A: Stateless, scalable, no server-side session storage needed
- **Q: What happens when token expires?** → A: API returns 401, frontend clears storage and redirects to login
- **Q: Is the token secure in localStorage?** → A: For this project scope yes, but production would use HttpOnly cookies to prevent XSS
- **Q: What algorithm is used?** → A: HMAC-SHA256 (`HS256`) — symmetric key signing