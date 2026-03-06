package com.vms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssociateVisitCount {
        private String associateName;
        private long visitCount;
    }
}
