## 🗄️ Member 7 — Core Entities, Repositories & API Integration Layer

**Owner:** _[Name]_

### Files
| Layer | Files |
|-------|-------|
| Backend Entities | `Visitor.java`, `VisitRequest.java`, `VisitLog.java` |
| Backend Repos | `VisitorRepository.java`, `VisitRequestRepository.java`, `VisitLogRepository.java` |
| Backend Services | `VisitorService.java`, `VisitRequestService.java`, `VisitLogService.java` |
| Frontend | `visitor.service.ts`, `visit-request.service.ts`, `visit-log.service.ts` |
| Models | `vms.models.ts` — all interfaces and enums |

### Code Walkthrough — Backend Entities

#### Visitor.java — Visitor Entity
```java
@Entity
@Table(name = "visitors")
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;                    // Visitor's full name
    private String email;                   // Contact email
    private String mobileNumber;            // Contact phone
    private String company;                 // Visitor's organization

    @Lob
    private byte[] photo;                   // Visitor photo stored as binary blob
    //     ^^^^
    //     @Lob = Large Object — tells JPA to use BLOB column type in database
    //     Stores the raw image bytes directly in the database

    @OneToMany(mappedBy = "visitor")
    private List<VisitRequest> visitRequests;  // One visitor can have many visit requests
    // mappedBy = "visitor" means VisitRequest owns the relationship (has the FK column)
}
```

#### VisitRequest.java — Visit Request Entity
```java
@Entity
@Table(name = "visit_requests")
public class VisitRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;            // The visitor this request is for
    //     FetchType.LAZY = visitor data loaded only when accessed (performance optimization)
    //     @JoinColumn = creates "visitor_id" foreign key column in visit_requests table

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private User requestedBy;           // The associate who created this request

    private String purpose;             // Purpose of visit (e.g., "Client Meeting")
    private LocalDate fromDate;         // Visit start date
    private LocalDate toDate;           // Visit end date

    @Enumerated(EnumType.STRING)
    private VisitStatus status;         // PENDING → APPROVED/REJECTED/CANCELLED/COMPLETED

    private String remarks;             // Admin's remarks upon approval/rejection
    private LocalDateTime createdAt;    // When the request was created
    private LocalDateTime updatedAt;    // When the status was last changed
}
```

#### VisitLog.java — Check-in/Check-out Log
```java
@Entity
@Table(name = "visit_logs")
public class VisitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_request_id")
    private VisitRequest visitRequest;    // Links to the approved visit request

    private LocalDateTime checkInTime;    // When visitor entered (set during QR scan check-in)
    private LocalDateTime checkOutTime;   // When visitor left (set during QR scan check-out)
    //     checkOutTime = null means visitor is still on premises

    private String securityGuardName;     // Name of the security person who processed the scan
}
// KEY QUERY: "Active visitors" = SELECT * FROM visit_logs WHERE check_out_time IS NULL
```

### Code Walkthrough — Entity Relationships (ER Diagram)
```
┌──────────┐     1:N     ┌────────────────┐     1:1     ┌──────────┐
│  Visitor  │────────────▶│ VisitRequest   │────────────▶│ VisitLog │
│           │             │                │             │          │
│ id        │             │ id             │             │ id       │
│ name      │             │ visitor_id (FK)│             │ visit_   │
│ email     │             │ requested_by   │             │ request_ │
│ mobile    │             │   (FK → User)  │             │ id (FK)  │
│ company   │             │ purpose        │             │ checkIn  │
│ photo     │             │ fromDate       │             │ checkOut │
└──────────┘             │ toDate         │             └──────────┘
                         │ status         │
                         │ remarks        │
      ┌──────────┐       │ createdAt      │
      │   User   │───────│ updatedAt      │
      │          │  1:N  └────────────────┘
      │ id       │
      │ name     │  (User as Associate creates VisitRequests)
      │ email    │  (User as Admin approves/rejects them)
      │ role     │  (User as Security processes VisitLogs)
      └──────────┘
```

### Code Walkthrough — Frontend Models

#### vms.models.ts — All TypeScript Interfaces
```typescript
// What it does: Defines all data types used throughout the Angular application

export enum Role {
    ADMIN = 'ADMIN',
    ASSOCIATE = 'ASSOCIATE',
    SECURITY = 'SECURITY'
}

export enum VisitStatus {
    PENDING = 'PENDING',       // Request created, awaiting admin review
    APPROVED = 'APPROVED',     // Admin approved — QR code generated
    REJECTED = 'REJECTED',     // Admin rejected with remarks
    CANCELLED = 'CANCELLED',   // Associate cancelled their own request
    COMPLETED = 'COMPLETED'    // Visit finished (checked out)
}

// Generic API response wrapper — EVERY backend endpoint returns this format
export interface ApiResponse<T> {
    success: boolean;     // true/false
    message: string;      // "Request approved" or "Invalid data"
    data: T;              // The actual payload — type varies per endpoint
}
// Usage: ApiResponse<User> → { success: true, message: "...", data: { id: 1, name: "John" } }
// Usage: ApiResponse<VisitRequest[]> → { success: true, message: "...", data: [{...}, {...}] }

export interface AuthResponse {
    token: string;        // JWT token string
    type: string;         // "Bearer"
    id: number;           // User ID
    name: string;         // User display name
    email: string;        // User email
    role: Role;           // User role
}
```

### Code Walkthrough — Frontend Services

#### visit-request.service.ts
```typescript
@Injectable({ providedIn: 'root' })
export class VisitRequestService {
    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient) {}

    // What it does: Creates a new visit request (Associate dashboard form submission)
    createRequest(formData: FormData): Observable<ApiResponse<VisitRequest>> {
        return this.http.post<ApiResponse<VisitRequest>>(
            `${this.apiUrl}/visit-requests`, formData
            // FormData → multipart/form-data encoding (because of photo file)
            // Angular HttpClient auto-sets Content-Type for FormData
        );
    }

    // What it does: Gets requests created by the currently logged-in user
    getMyRequests(): Observable<ApiResponse<VisitRequest[]>> {
        return this.http.get<ApiResponse<VisitRequest[]>>(
            `${this.apiUrl}/visit-requests/my`
            // Backend reads JWT → extracts user ID → filters requests by requestedBy
        );
    }

    // What it does: Gets ALL requests (Admin only)
    getAllRequests(): Observable<ApiResponse<VisitRequest[]>> {
        return this.http.get<ApiResponse<VisitRequest[]>>(
            `${this.apiUrl}/visit-requests`
        );
    }

    // What it does: Updates request status (Admin approve/reject)
    updateStatus(id: number, payload: any): Observable<ApiResponse<VisitRequest>> {
        return this.http.put<ApiResponse<VisitRequest>>(
            `${this.apiUrl}/visit-requests/${id}/status`, payload
        );
    }
}
```

### Code Walkthrough — Repository Layer

#### VisitRequestRepository.java
```java
// What it does: Spring Data JPA repository — auto-generates SQL from method names
@Repository
public interface VisitRequestRepository extends JpaRepository<VisitRequest, Long> {

    // Method name → SQL: SELECT * FROM visit_requests WHERE requested_by = ?
    List<VisitRequest> findByRequestedBy(User user);

    // Method name → SQL: SELECT * FROM visit_requests WHERE status = ?
    List<VisitRequest> findByStatus(VisitStatus status);

    // Method name → SQL: SELECT * FROM visit_requests WHERE status = ? AND from_date BETWEEN ? AND ?
    List<VisitRequest> findByStatusAndFromDateBetween(
        VisitStatus status, LocalDate start, LocalDate end
    );

    // Count queries for dashboard stats
    long countByStatus(VisitStatus status);
    // → SQL: SELECT COUNT(*) FROM visit_requests WHERE status = 'PENDING'
}
// Spring Data JPA reads the method name, parses it, and generates the implementation at runtime
// No SQL writing needed — "Convention over Configuration"
```

### Demo Script
1. Open each entity file → Walk through annotations (`@Entity`, `@ManyToOne`, `@JoinColumn`)
2. Draw the ER diagram on whiteboard (or show the ASCII diagram above)
3. Open H2/MySQL console → Show actual table structures and foreign keys
4. Open `vms.models.ts` → Show how TypeScript interfaces mirror Java entities
5. Open a service file → Show HTTP client calls and how `ApiResponse<T>` is used
6. Show repository → Explain derived query method naming convention

### Questions They Might Ask
- **Q: Why `@ManyToOne(fetch = LAZY)`?** → A: Prevents loading all related data every time — loads only when accessed (N+1 prevention)
- **Q: Why `ApiResponse<T>` wrapper?** → A: Consistent format — frontend always knows to check `response.success` and access `response.data`
- **Q: How does Spring Data JPA generate queries from method names?** → A: Parses method name by keywords (`findBy`, `And`, `Between`, `OrderBy`) → builds JPA criteria query
- **Q: Why store photo as `@Lob byte[]`?** → A: Simple approach; production might use file storage (S3) with DB storing just the URL
- **Q: What is the cascade strategy?** → A: Depends on business rules — deleting a visitor should not delete visit logs (audit trail)

---