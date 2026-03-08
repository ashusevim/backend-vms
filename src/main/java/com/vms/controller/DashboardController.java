package com.vms.controller;

import com.vms.dto.response.ApiResponse;
import com.vms.dto.response.DashboardResponse;
import com.vms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for admin dashboard and analytics endpoints.
 *
 * <p>All endpoints require the {@code ADMIN} role. Provides real-time
 * statistics and custom date-range analytics for visit management.</p>
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Retrieves current dashboard statistics (today and current week).
     *
     * @return a {@code 200 OK} response with dashboard metrics
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardStats()));
    }

    /**
     * Retrieves analytics data for a custom date range.
     *
     * @param from the start date (ISO format)
     * @param to   the end date (ISO format)
     * @return a {@code 200 OK} response with analytics data
     */
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<DashboardResponse>> getAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAnalytics(from, to)));
    }
}
