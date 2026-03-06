package com.vms.service.impl;

import com.vms.dto.response.DashboardResponse;
import com.vms.enums.VisitStatus;
import com.vms.repository.VisitLogRepository;
import com.vms.repository.VisitRequestRepository;
import com.vms.repository.VisitorRepository;
import com.vms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VisitRequestRepository visitRequestRepository;
    private final VisitLogRepository visitLogRepository;
    private final VisitorRepository visitorRepository;

    @Override
    public DashboardResponse getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);

        return DashboardResponse.builder()
                .totalApprovals(visitRequestRepository.countByStatus(VisitStatus.APPROVED))
                .totalPending(visitRequestRepository.countByStatus(VisitStatus.PENDING))
                .totalRejected(visitRequestRepository.countByStatus(VisitStatus.REJECTED))
                .totalVisitors(visitorRepository.count())
                .visitorsToday(visitRequestRepository.countByDate(today))
                .visitorsThisWeek(visitRequestRepository.countByDateRange(weekStart, today))
                .visitorsByDepartment(getDepartmentBreakdown())
                .peakVisitingHours(getPeakHours())
                .mostVisitedAssociates(getMostVisitedAssociates())
                .dailyVisitorCounts(getDailyCountsForWeek(weekStart, today))
                .approvedCount(visitRequestRepository.countByStatusAndDate(VisitStatus.APPROVED, today))
                .pendingCount(visitRequestRepository.countByStatusAndDate(VisitStatus.PENDING, today))
                .rejectedCount(visitRequestRepository.countByStatusAndDate(VisitStatus.REJECTED, today))
                .build();
    }

    @Override
    public DashboardResponse getAnalytics(LocalDate fromDate, LocalDate toDate) {
        long approved = visitRequestRepository.countByStatusAndDateRange(VisitStatus.APPROVED, fromDate, toDate);
        long pending = visitRequestRepository.countByStatusAndDateRange(VisitStatus.PENDING, fromDate, toDate);
        long rejected = visitRequestRepository.countByStatusAndDateRange(VisitStatus.REJECTED, fromDate, toDate);
        long total = visitRequestRepository.countByDateRange(fromDate, toDate);

        return DashboardResponse.builder()
                .totalApprovals(approved)
                .totalPending(pending)
                .totalRejected(rejected)
                .totalVisitors(total)
                .visitorsToday(total)
                .visitorsThisWeek(total)
                .visitorsByDepartment(getDepartmentBreakdown())
                .peakVisitingHours(getPeakHoursForRange(fromDate, toDate))
                .mostVisitedAssociates(getMostVisitedAssociates())
                .dailyVisitorCounts(getDailyCountsForWeek(fromDate, toDate))
                .approvedCount(approved)
                .pendingCount(pending)
                .rejectedCount(rejected)
                .build();
    }

    private Map<String, Long> getDepartmentBreakdown() {
        List<Object[]> results = visitRequestRepository.countByDepartment();
        Map<String, Long> breakdown = new LinkedHashMap<>();
        for (Object[] row : results) {
            String dept = row[0] != null ? row[0].toString() : "Unknown";
            Long count = ((Number) row[1]).longValue();
            breakdown.put(dept, count);
        }
        return breakdown;
    }

    private Map<Integer, Long> getPeakHours() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return getPeakHoursInternal(start, end);
    }

    private Map<Integer, Long> getPeakHoursForRange(LocalDate from, LocalDate to) {
        return getPeakHoursInternal(from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }

    private Map<Integer, Long> getPeakHoursInternal(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = visitLogRepository.findPeakVisitingHours(start, end);
        Map<Integer, Long> peakHours = new LinkedHashMap<>();
        for (Object[] row : results) {
            Integer hour = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            peakHours.put(hour, count);
        }
        return peakHours;
    }

    private List<DashboardResponse.AssociateVisitCount> getMostVisitedAssociates() {
        List<Object[]> results = visitRequestRepository.findMostVisitedAssociates();
        return results.stream()
                .limit(10)
                .map(row -> DashboardResponse.AssociateVisitCount.builder()
                        .associateName(row[0].toString())
                        .visitCount(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, Long> getDailyCountsForWeek(LocalDate from, LocalDate to) {
        List<Object[]> results = visitRequestRepository.countPerDay(from, to);
        Map<String, Long> dailyCounts = new LinkedHashMap<>();
        // Initialize all days in range with 0
        LocalDate current = from;
        while (!current.isAfter(to)) {
            String dayName = current.getDayOfWeek().toString().substring(0, 3);
            // If same day name already exists (multi-week), append date
            String key = dayName;
            if (dailyCounts.containsKey(key)) {
                key = current.toString();
            }
            dailyCounts.put(key, 0L);
            current = current.plusDays(1);
        }
        // Fill in actual counts
        for (Object[] row : results) {
            LocalDate date = (LocalDate) row[0];
            Long count = ((Number) row[1]).longValue();
            String dayName = date.getDayOfWeek().toString().substring(0, 3);
            if (dailyCounts.containsKey(dayName)) {
                dailyCounts.put(dayName, count);
            } else {
                dailyCounts.put(date.toString(), count);
            }
        }
        return dailyCounts;
    }
}
