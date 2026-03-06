package com.vms.service;

import com.vms.dto.response.DashboardResponse;

import java.time.LocalDate;

public interface DashboardService {
    DashboardResponse getDashboardStats();

    DashboardResponse getAnalytics(LocalDate fromDate, LocalDate toDate);
}
