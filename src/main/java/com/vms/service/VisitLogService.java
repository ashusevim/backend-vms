package com.vms.service;

import com.vms.dto.request.CheckInRequest;
import com.vms.dto.response.VisitLogResponse;

import java.util.List;

public interface VisitLogService {
    VisitLogResponse checkIn(CheckInRequest request, Long securityGuardId);

    VisitLogResponse checkOut(Long visitLogId);

    VisitLogResponse getVisitLogById(Long id);

    List<VisitLogResponse> getActiveVisits();

    List<VisitLogResponse> getAllVisitLogs();
}
