package com.vms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO containing aggregated dashboard and analytics data.
 *
 * <p>Used by the admin dashboard to display key metrics such as approval counts,
 * daily visitor trends, department-wise breakdowns, peak visiting hours, and
 * the most-visited associates.</p>
 *
 * @see com.vms.service.DashboardService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long totalApprovals;
    private long totalPending;
    private long totalRejected;
    private long totalVisitors;
    private long visitorsToday;
    private long visitorsThisWeek;

    // Per-status counts for selected date/range
    private long approvedCount;
    private long pendingCount;
    private long rejectedCount;

    // Daily visitor counts for the week: {"Mon" -> 5, "Tue" -> 3, ...}
    private Map<String, Long> dailyVisitorCounts;

    // Department-wise breakdown: {department -> count}
    private Map<String, Long> visitorsByDepartment;

    // Peak visiting hours: {hour -> count}
    private Map<Integer, Long> peakVisitingHours;

    // Most visited associates: [{name, count}]
    private List<AssociateVisitCount> mostVisitedAssociates;

    /**
     * Nested DTO representing a single associate and their total visit count.
     *
     * <p>Used in the "Most Visited Associates" leaderboard section of the dashboard.</p>
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssociateVisitCount {
        private String associateName;
        private long visitCount;
    }
}
