## 📊 Member 3 — Admin Dashboard & Analytics

**Owner:** _[Name]_

### Files
| Layer | Files |
|-------|-------|
| Frontend | `admin-dashboard.component.ts` |
| Service | `dashboard.service.ts` |
| Libraries | `ng2-charts`, `chart.js`, `luxon` |

### Code Walkthrough — Frontend

#### admin-dashboard.component.ts — Signal-Based State Management
```typescript
// What it does: Declares reactive state using Angular Signals (modern alternative to Observables for UI state)
export class AdminDashboardComponent implements OnInit {
    stats = signal<DashboardResponse | null>(null);  // Holds dashboard statistics
    selectedDate = signal<DateTime>(DateTime.now());  // Currently selected date (Luxon DateTime)
    currentMonthLabel = signal<string>('');           // Display label like "March 2026"

    // What it does: Changes are automatically reflected in the template
    // When stats() changes → all {{ stats().totalRequests }} bindings update
}
```

#### Dashboard Stats Cards
```typescript
// In the template — What it does: Displays stat cards with dynamic values
`<div class="grid grid-cols-1 md:grid-cols-4 gap-6">
    <div class="bg-white rounded-2xl shadow-lg p-6">
        <h3 class="text-gray-500 text-sm">Total Requests</h3>
        <p class="text-3xl font-black text-primary-600">{{ stats()?.totalRequests }}</p>
        <!--                                           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
             stats() calls the signal → returns current value or null
             ?. is optional chaining → prevents error if stats is null (loading state)  -->
    </div>

    <div class="bg-white rounded-2xl shadow-lg p-6">
        <h3 class="text-gray-500 text-sm">Pending</h3>
        <p class="text-3xl font-black text-yellow-500">{{ stats()?.pendingCount }}</p>
        <!-- Yellow color = visual indicator for "needs action" -->
    </div>

    <div class="bg-white rounded-2xl shadow-lg p-6">
        <h3 class="text-gray-500 text-sm">Approved</h3>
        <p class="text-3xl font-black text-green-500">{{ stats()?.approvedCount }}</p>
    </div>

    <div class="bg-white rounded-2xl shadow-lg p-6">
        <h3 class="text-gray-500 text-sm">Rejected</h3>
        <p class="text-3xl font-black text-red-500">{{ stats()?.rejectedCount }}</p>
    </div>
</div>`
```

#### Date Navigation with Luxon
```typescript
// What it does: Navigates calendar months for date-filtered stats
prevMonth(): void {
    this.selectedDate.update(d => d.minus({ months: 1 }));  // Goes back one month
    this.updateMonthLabel();
    this.loadStats();   // Re-fetches stats for the new month
}

nextMonth(): void {
    this.selectedDate.update(d => d.plus({ months: 1 }));   // Goes forward one month
    this.updateMonthLabel();
    this.loadStats();
}

updateMonthLabel(): void {
    // Converts DateTime to readable format: "March 2026"
    this.currentMonthLabel.set(this.selectedDate().toFormat('LLLL yyyy'));
}
```

#### Chart Configuration
```typescript
// What it does: Configures Chart.js bar chart for visit trends
chartConfig: ChartConfiguration<'bar'> = {
    type: 'bar',
    data: {
        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],  // X-axis labels
        datasets: [{
            data: [12, 19, 3, 5, 2, 3, 7],     // Y-axis values (replaced by API data)
            backgroundColor: '#0ea5e9',          // Primary sky-blue color from Tailwind config
            borderRadius: 8,                     // Rounded bar tops
        }]
    },
    options: {
        responsive: true,                        // Chart resizes with container
        plugins: { legend: { display: false } }  // Hides legend to save space
    }
};
```

#### dashboard.service.ts — API Calls
```typescript
// What it does: Fetches aggregated stats from backend with date filter
getStats(date: string): Observable<ApiResponse<DashboardResponse>> {
    return this.http.get<ApiResponse<DashboardResponse>>(
        `${this.apiUrl}/dashboard/stats?date=${date}`
        //                                ^^^^^^^^^^^
        // Query param sends selected date to backend
        // Backend filters visit requests by this date range
    );
}

// What it does: Fetches daily visit counts for chart rendering
getWeeklyTrends(date: string): Observable<ApiResponse<number[]>> {
    return this.http.get<ApiResponse<number[]>>(
        `${this.apiUrl}/dashboard/weekly-trends?date=${date}`
    );
}
```

### Demo Script
1. Navigate to `/admin/dashboard` → Show the 4 stat cards with live data
2. Open DevTools → Network tab → Show the API call with date query parameter
3. Click prev/next month arrows → Show stats updating reactively
4. Point to the chart → Explain Chart.js integration and data binding
5. Resize the browser window → Show responsive grid (4 cols → 2 cols → 1 col)

### Questions They Might Ask
- **Q: Why Angular Signals instead of BehaviorSubject?** → A: Simpler API, better change detection performance, Angular's recommended modern approach
- **Q: Why Luxon instead of native Date?** → A: Immutable, timezone-safe, better formatting API, `plus()`/`minus()` chainable methods
- **Q: How does the chart update?** → A: When `loadStats()` runs, it sets new data on the chart config → `chart.update()` re-renders
- **Q: What if there's no data for a month?** → A: API returns zeros, cards show 0, chart shows empty bars
