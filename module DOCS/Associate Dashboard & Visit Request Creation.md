## 📝 Member 5 — Associate Dashboard & Visit Request Creation

**Owner:** _[Name]_

### Files
| Layer | Files |
|-------|-------|
| Frontend | `associate-dashboard.component.ts` |
| Service | `visit-request.service.ts`, `visitor.service.ts` |
| Backend | Visit request creation API, Visitor search API |

### Code Walkthrough — Frontend

#### Visit Request Form — Reactive Forms
```typescript
// What it does: Defines the form structure with validation rules
visitForm = this.fb.group({
    // Visitor Details
    visitorName:  ['', Validators.required],                           // Cannot be empty
    visitorEmail: ['', [Validators.required, Validators.email]],       // Must be valid email format
    visitorMobile:['', [Validators.required, Validators.pattern(/^\d{10}$/)]],  // Exactly 10 digits
    visitorCompany: ['', Validators.required],

    // Visit Details
    purpose:  ['', Validators.required],                   // Why are they visiting?
    fromDate: ['', Validators.required],                   // Visit start date
    toDate:   ['', Validators.required],                   // Visit end date
});

// In template, validation errors show like:
// <input formControlName="visitorEmail">
// <span *ngIf="visitForm.get('visitorEmail')?.errors?.['email']">
//     Invalid email format
// </span>
```

#### Visitor Search — Auto-populate
```typescript
// What it does: Searches existing visitors by email to avoid duplicate entries
searchVisitor(): void {
    const email = this.visitForm.get('visitorEmail')?.value;
    if (!email) return;

    this.visitorService.searchByEmail(email).subscribe({
        next: (response) => {
            if (response.data) {
                const visitor = response.data;
                // Auto-fills the form with existing visitor data
                this.visitForm.patchValue({
                    visitorName: visitor.name,       // Pre-fills name
                    visitorMobile: visitor.mobile,   // Pre-fills mobile
                    visitorCompany: visitor.company   // Pre-fills company
                });
                // User doesn't have to re-type everything for returning visitors!
            }
        },
        error: () => {
            // No existing visitor found — user fills manually (new visitor)
        }
    });
}
```

#### Photo Upload
```typescript
// What it does: Handles visitor photo file selection with validation
onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    // Validate file type
    if (!['image/jpeg', 'image/png'].includes(file.type)) {
        this.photoError = 'Only JPG and PNG files are allowed';
        return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {  // 5 * 1024 * 1024 = 5,242,880 bytes
        this.photoError = 'File size must be less than 5MB';
        return;
    }

    this.selectedPhoto = file;  // Stores the file for form submission
    this.photoError = '';

    // Preview the image
    const reader = new FileReader();
    reader.onload = () => {
        this.photoPreview.set(reader.result as string);  // Shows preview in <img> tag
    };
    reader.readAsDataURL(file);  // Converts file to base64 data URL for preview
}
```

#### Form Submission — Multipart Request
```typescript
// What it does: Submits the visit request with all form data + photo as multipart
onSubmit(): void {
    if (this.visitForm.invalid) return;

    const formData = new FormData();  // FormData allows mixing text fields + files

    // Append text fields
    formData.append('visitorName', this.visitForm.value.visitorName!);
    formData.append('visitorEmail', this.visitForm.value.visitorEmail!);
    formData.append('visitorMobile', this.visitForm.value.visitorMobile!);
    formData.append('visitorCompany', this.visitForm.value.visitorCompany!);
    formData.append('purpose', this.visitForm.value.purpose!);
    formData.append('fromDate', this.visitForm.value.fromDate!);
    formData.append('toDate', this.visitForm.value.toDate!);

    // Append photo file (if selected)
    if (this.selectedPhoto) {
        formData.append('photo', this.selectedPhoto);  // Binary file data
    }

    this.visitRequestService.createRequest(formData).subscribe({
        next: () => {
            this.toastMessage = 'Visit request submitted successfully!';
            this.visitForm.reset();       // Clears the form
            this.photoPreview.set(null);  // Removes photo preview
            this.loadMyRequests();        // Refreshes the request list below
        },
        error: (err) => {
            this.toastMessage = 'Failed to submit request';
        }
    });
}
```

#### My Requests List
```typescript
// What it does: Loads all visit requests created by the currently logged-in associate
loadMyRequests(): void {
    this.visitRequestService.getMyRequests().subscribe({
        next: (response) => {
            this.myRequests.set(response.data);
            // Displayed in a table below the form:
            // | Visitor     | Purpose  | Date          | Status    |
            // |-------------|----------|---------------|-----------|
            // | John Doe    | Meeting  | Mar 10 - 12   | PENDING   |
            // | Jane Smith  | Interview| Mar 15        | APPROVED  |
        }
    });
}
```

### Demo Script
1. Navigate to `/associate/dashboard` → Show the visit request form
2. Type an existing visitor's email → Click search → Show auto-populate
3. Upload a photo → Show preview, try uploading a 10MB file → Show error
4. Fill all fields → Submit → Show success toast
5. Scroll down → Show the request appears in "My Requests" table with PENDING status
6. Show form validation — submit with empty fields → Show red error messages

### Questions They Might Ask
- **Q: Why use FormData instead of JSON?** → A: JSON cannot carry binary files; FormData supports `multipart/form-data` encoding
- **Q: What happens to the uploaded photo?** → A: Backend stores it (filesystem or DB), used for visitor ID/access card
- **Q: Can associates edit a submitted request?** → A: Only if it's still PENDING; once APPROVED/REJECTED, it's locked
- **Q: Why search visitors by email?** → A: Prevents duplicate visitor records, saves time for frequent visitors

---
