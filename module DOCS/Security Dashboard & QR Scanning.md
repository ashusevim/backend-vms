## 🔍 Member 6 — Security Dashboard & QR Scanning

**Owner:** _[Name]_

### Files
| Layer | Files |
|-------|-------|
| Frontend | `security-dashboard.component.ts` |
| Service | `visit-log.service.ts` |
| Libraries | `@zxing/ngx-scanner`, `@zxing/library` |

### Code Walkthrough — Frontend

#### QR Scanner Setup
```typescript
import { BarcodeFormat } from '@zxing/library';

export class SecurityDashboardComponent implements OnInit {
    // What it does: Restricts scanner to QR codes only (ignores barcodes, Data Matrix, etc.)
    allowedFormats = [BarcodeFormat.QR_CODE];

    scannerEnabled = signal<boolean>(true);       // Controls camera on/off
    scanResult = signal<any>(null);               // Holds decoded QR data after scan
    error = signal<string>('');                    // Error message display
    activeVisitors = signal<VisitLog[]>([]);       // Currently on-premises visitors

    // Template scanner element:
    // <zxing-scanner
    //     [formats]="allowedFormats"         → Only scans QR codes
    //     (scanSuccess)="onScanSuccess($event)"  → Fires when QR is decoded
    //     [enable]="scannerEnabled()"        → Camera toggle
    // ></zxing-scanner>
}
```

#### Scan Success Handler
```typescript
// What it does: Processes the QR code content when a scan is successful
onScanSuccess(result: string): void {
    this.scannerEnabled.set(false);  // Pauses scanner to prevent duplicate scans

    // result = the decoded QR string (e.g., visit request ID or encoded visitor data)
    console.log('Scanned QR:', result);

    // Call backend to check-in or check-out the visitor
    this.visitLogService.processQRCode(result).subscribe({
        next: (response) => {
            this.scanResult.set(response.data);
            this.error.set('');
            this.loadActiveVisitors();  // Refresh the on-premises board

            // Shows green success card:
            // ┌─────────────────────────────────┐
            // │  ✅ Verification Successful      │
            // │  Name: John Doe                  │
            // │  Company: TCS                    │
            // │  Action: CHECK-IN                │
            // │  Time: 10:30 AM                  │
            // └─────────────────────────────────┘
        },
        error: (err) => {
            this.scanResult.set(null);
            this.error.set(err.error?.message || 'Invalid QR code');

            // Shows red error banner:
            // ┌─────────────────────────────────┐
            // │  ❌ Verification Failed           │
            // │  QR code is expired or invalid   │
            // └─────────────────────────────────┘
        }
    });
}
```

#### Resume Scanner
```typescript
// What it does: Re-enables the camera for the next scan
resumeScanner(): void {
    this.scannerEnabled.set(true);   // Turns camera back on
    this.scanResult.set(null);       // Clears the previous result card
    this.error.set('');              // Clears error messages
}
```

#### Active Visitors Board
```typescript
// What it does: Fetches all visitors who are currently checked-in but not checked-out
loadActiveVisitors(): void {
    this.visitLogService.getActiveVisitors().subscribe({
        next: (response) => {
            this.activeVisitors.set(response.data);
        }
    });
}

// In template — displays a scrollable list:
// ┌───────────────────────────────────────────┐
// │  🏢 Active Visitors On Premises    [12]   │
// ├───────────────────────────────────────────┤
// │  👤 John Doe      | TCS     | 10:30 AM   │
// │  👤 Jane Smith    | Infosys | 11:15 AM   │
// │  👤 Bob Johnson   | Wipro   | 09:45 AM   │
// │  ... (scrollable)                         │
// └───────────────────────────────────────────┘
```

#### CSS Scan Animation
```typescript
// What it does: Creates the animated scanning line effect over the camera feed
// In component styles:
`
@keyframes scan {
    0%   { top: 0%; }
    50%  { top: 100%; }
    100% { top: 0%; }
}
.scan-line {
    position: absolute;
    width: 100%;
    height: 2px;
    background: linear-gradient(90deg, transparent, #0ea5e9, transparent);
    animation: scan 2s ease-in-out infinite;
    /* Creates a blue line that moves up and down over the camera viewport */
}
`
```

#### visit-log.service.ts — API Calls
```typescript
// What it does: Sends scanned QR data to backend for check-in/check-out processing
processQRCode(qrData: string): Observable<ApiResponse<VisitLog>> {
    return this.http.post<ApiResponse<VisitLog>>(
        `${this.apiUrl}/visit-logs/scan`,
        { qrData }
        // Backend determines if this is a CHECK-IN or CHECK-OUT based on:
        // - If no active log exists → CHECK-IN (records entry time)
        // - If active log exists → CHECK-OUT (records exit time)
    );
}

// What it does: Gets all visitors with check-in but no check-out yet
getActiveVisitors(): Observable<ApiResponse<VisitLog[]>> {
    return this.http.get<ApiResponse<VisitLog[]>>(
        `${this.apiUrl}/visit-logs/active`
        // SQL equivalent: SELECT * FROM visit_logs WHERE check_out_time IS NULL
    );
}
```

### Demo Script
1. Navigate to `/security/dashboard` → Show dark-themed camera interface
2. Point out the scan animation line over the camera viewport
3. Hold a QR code in front of the camera → Show successful scan
4. Show the green verification card with visitor details
5. Show the "Active Visitors" board updating with the new check-in
6. Scan again → Show CHECK-OUT action
7. Show the visitor removed from the active board
8. Scan an invalid QR → Show red error banner
9. Click "Resume" to re-enable the scanner

### Questions They Might Ask
- **Q: How does the system know check-in vs check-out?** → A: Backend checks if an active visit log exists for the visitor; if yes → check-out, if no → check-in
- **Q: What library is used for QR scanning?** → A: ZXing (`@zxing/ngx-scanner`) — open-source barcode/QR library ported from Java
- **Q: Does it work on mobile?** → A: Yes, ZXing accesses the device camera via browser's `getUserMedia()` API
- **Q: Why dark theme for scanner?** → A: Better contrast with camera feed, reduces glare, industry standard for camera-based UIs
- **Q: What data is encoded in the QR?** → A: Visit request ID or encoded visitor identifier — generated upon approval

---
