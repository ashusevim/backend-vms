package com.vms.service;

import com.vms.dto.request.ApproveRejectRequest;
import com.vms.dto.request.CreateVisitRequestDTO;
import com.vms.dto.response.VisitRequestResponse;
import com.vms.enums.VisitStatus;

import java.time.LocalDate;
import java.util.List;

public interface VisitRequestService {
    VisitRequestResponse createVisitRequest(CreateVisitRequestDTO request);

    VisitRequestResponse getVisitRequestById(Long id);

    List<VisitRequestResponse> getAllVisitRequests();

    List<VisitRequestResponse> getVisitRequestsByStatus(VisitStatus status);

    List<VisitRequestResponse> getVisitRequestsByDate(LocalDate date);

    List<VisitRequestResponse> getVisitRequestsByDateRange(LocalDate from, LocalDate to);

    List<VisitRequestResponse> getVisitRequestsByAssociate(Long associateId);

    List<VisitRequestResponse> getVisitRequestsByDepartment(String department);

    VisitRequestResponse approveOrReject(Long id, ApproveRejectRequest request, Long approvedByUserId);

    VisitRequestResponse cancelVisitRequest(Long id);

    byte[] getQRCode(Long id);
}
