## ✅ Member 4 — Visit Request Management (Admin Side)

**Owner:** _[Name]_

### Files
| Layer | Files |
|-------|-------|
| Frontend | `admin-visit-requests.component.ts` |
| Service | `visit-request.service.ts` |
| Backend | `VisitRequestController.java`, `VisitRequestService.java` |
| Models | `VisitStatus` enum |

### Code Walkthrough — Frontend

#### Request Listing & Status Badges
```typescript
// What it does: Returns Tailwind CSS classes based on visit request status
getStatusBadgeClass(status: string): string {
    switch (status) {
        case 'PENDING':   return 'bg-yellow-100 text-yellow-800';   // Yellow = awaiting action
        case 'APPROVED':  return 'bg-green-100 text-green-800';     // Green = approved
        case 'REJECTED':  return 'bg-red-100 text-red-800';         // Red = rejected
        case 'CANCELLED': return 'bg-gray-100 text-gray-800';       // Gray = cancelled
        case 'COMPLETED': return 'bg-blue-100 text-blue-800';       // Blue = visit completed
        default:          return 'bg-gray-100 text-gray-600';
    }
}
// USED IN TEMPLATE:
// <span [class]="getStatusBadgeClass(request.status)">{{ request.status }}</span>
// This creates color-coded pills like:  [PENDING]  [APPROVED]  [REJECTED]
```

#### Detail Modal — View Full Request
```typescript
// What it does: Opens a detail panel showing full visitor + visit information
selectedRequest = signal<VisitRequest | null>(null);  // Currently selected request
showDetail = signal<boolean>(false);                   // Controls modal visibility

openDetail(request: VisitRequest): void {
    this.selectedRequest.set(request);  // Sets the request data
    this.showDetail.set(true);          // Shows the modal/panel
    // Template reads: selectedRequest()?.visitor.name, selectedRequest()?.purpose, etc.
}

// In template — the detail panel shows:
// ┌──────────────────────────────────┐
// │ Visitor: John Doe                │
// │ Email: john@example.com          │
// │ Mobile: +91-9876543210           │
// │ Company: TCS                     │
// │ Purpose: Client Meeting          │
// │ Date: March 10, 2026 - March 12 │
// │ Status: [PENDING]                │
// │                                  │
// │ [Approve]  [Reject]              │
// └──────────────────────────────────┘
```

#### Approve/Reject Workflow
```typescript
// What it does: Opens the approve/reject dialog with a remarks text area
showApproveReject = signal<boolean>(false);
actionType = signal<'APPROVED' | 'REJECTED'>('APPROVED');
remarks = signal<string>('');

openApproveReject(request: VisitRequest, action: 'APPROVED' | 'REJECTED'): void {
    this.selectedRequest.set(request);
    this.actionType.set(action);         // Determines if we're approving or rejecting
    this.remarks.set('');                // Clears previous remarks
    this.showApproveReject.set(true);    // Shows the dialog
}

// What it does: Sends the approval/rejection to the backend
submitAction(): void {
    const requestId = this.selectedRequest()!.id;
    const payload = {
        status: this.actionType(),       // 'APPROVED' or 'REJECTED'
        remarks: this.remarks()          // Admin's reason/comment
    };

    this.visitRequestService.updateStatus(requestId, payload).subscribe({
        next: () => {
            this.showApproveReject.set(false);  // Closes the dialog
            this.loadRequests();                 // Refreshes the list to show updated status
            // Toast: "Request approved successfully"
        },
        error: (err) => {
            // Toast: "Failed to update request"
        }
    });
}
```

### Code Walkthrough — Backend

#### VisitRequestController.java — API Endpoints
```java
// What it does: Endpoint for admin to approve/reject a visit request
@PutMapping("/{id}/status")
@PreAuthorize("hasRole('ADMIN')")  // ONLY admins can access this endpoint
public ResponseEntity<ApiResponse<VisitRequestResponse>> updateStatus(
        @PathVariable Long id,               // Request ID from URL: /api/visit-requests/5/status
        @RequestBody StatusUpdateRequest req  // { status: "APPROVED", remarks: "Looks good" }
) {
    VisitRequestResponse updated = visitRequestService.updateStatus(id, req);
    return ResponseEntity.ok(ApiResponse.success("Status updated", updated));
}

// What it does: Fetches all visit requests for admin review
@GetMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getAllRequests() {
    return ResponseEntity.ok(ApiResponse.success("All requests", visitRequestService.getAll()));
}
```

#### VisitRequestService.java — Status Update Logic
```java
// What it does: Updates the status of a visit request with validation
public VisitRequestResponse updateStatus(Long id, StatusUpdateRequest req) {
    VisitRequest request = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Request not found"));  // 404 if invalid ID

    // Validate status transition — can only approve/reject PENDING requests
    if (request.getStatus() != VisitStatus.PENDING) {
        throw new RuntimeException("Can only update PENDING requests");
    }

    request.setStatus(VisitStatus.valueOf(req.getStatus()));  // "APPROVED" string → enum
    request.setRemarks(req.getRemarks());                      // Stores admin's remarks
    request.setUpdatedAt(LocalDateTime.now());                 // Timestamp of action

    VisitRequest saved = repository.save(request);  // Persists to database
    return mapToResponse(saved);
}
```

### Demo Script
1. Show the requests table → Point out status badges with different colors
2. Click a request → Show detail panel with all visitor information
3. Click "Approve" → Show remarks dialog → Type a remark → Submit
4. Show the status changing from PENDING (yellow) to APPROVED (green) in the table
5. Open Postman → Try to approve an already-approved request → Show validation error
6. Try accessing the endpoint without ADMIN role → Show 403 Forbidden

### Questions They Might Ask
- **Q: Can an approved request be rejected later?** → A: No, only PENDING requests can be updated (business rule)
- **Q: Why are remarks required?** → A: Audit trail — admin must justify approval/rejection
- **Q: What happens after approval?** → A: QR code is generated for the visitor, enabling security check-in
- **Q: How is `@PreAuthorize` checked?** → A: Spring Security reads the role from JWT → SecurityContext → checks against annotation

---
