package com.vms.service;

import com.vms.dto.response.DashboardResponse;

import java.time.LocalDate;

/**
 * Service interface for retrieving dashboard statistics and analytics data.
 *
 * <p>Provides aggregated metrics for the admin dashboard, including visit counts,
 * department breakdowns, peak hours, and trending associates.</p>
 */
public interface DashboardService {

    /**
     * Retrieves the current dashboard statistics based on today's date and the current week.
     *
     * @return a {@link DashboardResponse} containing all dashboard metrics
     */
    DashboardResponse getDashboardStats();

    /**
     * Retrieves analytics data for a custom date range.
     *
     * @param fromDate the start date of the analysis period (inclusive)
     * @param toDate   the end date of the analysis period (inclusive)
     * @return a {@link DashboardResponse} containing analytics for the specified range
     */
    DashboardResponse getAnalytics(LocalDate fromDate, LocalDate toDate);
}
