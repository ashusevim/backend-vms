## 👤 Member 2 — User Management

**Owner:** _[Name]_

### Files
| Layer | Files |
|-------|-------|
| Backend | `User.java`, `UserService.java`, `UserRepository.java`, `UserController.java` |
| DTOs | `UserResponse.java`, `RegisterRequest.java`, `LoginRequest.java` |
| Models | `User` interface in `vms.models.ts` |

### Code Walkthrough — Backend

#### User.java — Entity Definition
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // Auto-generated primary key

    @Column(nullable = false)
    private String name;                // Full name of the user

    @Column(nullable = false, unique = true)
    private String email;               // Login email — must be unique across all users

    @Column(nullable = false)
    private String password;            // BCrypt hashed password — NEVER stored in plain text

    @Enumerated(EnumType.STRING)
    private Role role;                  // ADMIN, ASSOCIATE, or SECURITY — stored as string in DB

    private String mobileNumber;        // Contact number
    private String department;          // Department (e.g., IT, HR, Finance)
    private String designation;         // Job title (e.g., Manager, Developer)
}
// KEY POINT: @Enumerated(EnumType.STRING) stores "ADMIN" not 0 — readable in DB
```

#### UserService.java — Business Logic
```java
// What it does: Registers a new user after validation and password hashing
public UserResponse registerUser(RegisterRequest request) {
    // Step 1: Check if email already exists
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new RuntimeException("Email already registered");
    }

    // Step 2: Create user entity and hash the password
    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));  // BCrypt hashing
    user.setRole(request.getRole());
    user.setMobileNumber(request.getMobileNumber());
    user.setDepartment(request.getDepartment());
    user.setDesignation(request.getDesignation());

    // Step 3: Save to database
    User savedUser = userRepository.save(user);

    // Step 4: Return DTO (NOT the entity — password is excluded)
    return mapToUserResponse(savedUser);
}

// What it does: Converts entity to DTO — strips sensitive data like password
private UserResponse mapToUserResponse(User user) {
    return new UserResponse(
        user.getId(), user.getName(), user.getEmail(),
        user.getRole().name(), user.getMobileNumber(),
        user.getDepartment(), user.getDesignation()
    );
    // NOTE: password is NOT included in UserResponse — security best practice
}
```

#### UserResponse.java — DTO Pattern
```java
// What it does: Data Transfer Object — controls what data leaves the API
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String mobileNumber;
    private String department;
    private String designation;
    // NO password field — this is the whole point of DTOs
    // Entity has password → DTO does NOT → API response is safe
}
```

#### Role.java — Enum
```java
public enum Role {
    ADMIN,      // Can view dashboard analytics, approve/reject visit requests
    ASSOCIATE,  // Can create visit requests for visitors
    SECURITY    // Can scan QR codes, manage check-in/check-out
}
// Each role maps to a different Angular dashboard component after login
```

### Code Walkthrough — Frontend

#### User Model (vms.models.ts)
```typescript
// What it does: TypeScript interface mirroring the backend UserResponse DTO
export interface User {
    id: number;
    name: string;
    email: string;
    role: Role;
    mobileNumber: string;
    department: string;
    designation: string;
}
// This ensures type safety — TypeScript will catch if you access user.password (doesn't exist)
```

### Demo Script
1. Show `User.java` entity → Explain each annotation (`@Entity`, `@Table`, `@Column`, `@Enumerated`)
2. Open MySQL/H2 console → Show `users` table structure → Show hashed password in DB
3. Register a user via Postman → Show the `UserResponse` (no password in response)
4. Show `Role.java` enum → Explain how it maps to frontend routing

### Questions They Might Ask
- **Q: Why use DTOs instead of returning entities directly?** → A: Prevents password leakage, controls API contract, decouples DB schema from API
- **Q: Why `EnumType.STRING` instead of `EnumType.ORDINAL`?** → A: String is readable in DB and safe if enum order changes
- **Q: What if two users register with same email simultaneously?** → A: `unique = true` DB constraint prevents duplicates; service layer also checks
- **Q: Why BCrypt?** → A: Adaptive hashing — automatically salted, configurable cost factor, industry standard

---
