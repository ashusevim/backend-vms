package com.vms.service.impl;

import com.vms.dto.request.CheckInRequest;
import com.vms.dto.response.VisitLogResponse;
import com.vms.entity.User;
import com.vms.entity.VisitLog;
import com.vms.entity.VisitRequest;
import com.vms.enums.VisitStatus;
import com.vms.exception.BadRequestException;
import com.vms.exception.ResourceNotFoundException;
import com.vms.repository.UserRepository;
import com.vms.repository.VisitLogRepository;
import com.vms.repository.VisitRequestRepository;
import com.vms.service.AuditLogService;
import com.vms.service.NotificationService;
import com.vms.service.VisitLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link VisitLogService} managing visitor check-in/check-out operations.
 *
 * <p>Supports check-in via QR code token or visit request ID. On check-in, the
 * associate is notified; on check-out, the visit request status is set to COMPLETED.
 * All actions are recorded in the audit log.</p>
 *
 * @see VisitLogService
 */
@Service
@RequiredArgsConstructor
public class VisitLogServiceImpl implements VisitLogService {

    private final VisitLogRepository visitLogRepository;
    private final VisitRequestRepository visitRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public VisitLogResponse checkIn(CheckInRequest request, Long securityGuardId) {
        VisitRequest visitRequest;

        // Support check-in by QR code token or visit request ID
        if (StringUtils.hasText(request.getQrCodeToken())) {
            visitRequest = visitRequestRepository.findByQrCodeToken(request.getQrCodeToken())
                    .orElseThrow(() -> new ResourceNotFoundException("VisitRequest", "qrCodeToken",
                            request.getQrCodeToken()));
        } else if (request.getVisitRequestId() != null) {
            visitRequest = visitRequestRepository.findById(request.getVisitRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("VisitRequest", "id",
                            request.getVisitRequestId()));
        } else {
            throw new BadRequestException("Either visitRequestId or qrCodeToken must be provided");
        }

        // Validate status
        if (visitRequest.getStatus() != VisitStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED visit requests can be checked in. " +
                    "Current status: " + visitRequest.getStatus());
        }

        // Check if already checked in
        if (visitLogRepository.findByVisitRequestId(visitRequest.getId()).isPresent()) {
            throw new BadRequestException("Visitor is already checked in for this visit request");
        }

        User securityGuard = userRepository.findById(securityGuardId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", securityGuardId));

        VisitLog visitLog = VisitLog.builder()
                .visitRequest(visitRequest)
                .checkInTime(LocalDateTime.now())
                .securityGuard(securityGuard)
                .badgeNumber(request.getBadgeNumber())
                .build();

        visitLog = visitLogRepository.save(visitLog);

        // Notify associate
        notificationService.sendNotification(
                visitRequest.getAssociate().getId(),
                "Visitor Checked In",
                "Visitor " + visitRequest.getVisitor().getName() + " has checked in");

        auditLogService.log(securityGuardId, "CHECK_IN", "VisitLog",
                visitLog.getId(), "Visitor checked in: " + visitRequest.getVisitor().getName());

        return mapToResponse(visitLog);
    }

    @Override
    @Transactional
    public VisitLogResponse checkOut(Long visitLogId) {
        VisitLog visitLog = visitLogRepository.findById(visitLogId)
                .orElseThrow(() -> new ResourceNotFoundException("VisitLog", "id", visitLogId));

        if (visitLog.getCheckOutTime() != null) {
            throw new BadRequestException("Visitor is already checked out");
        }

        visitLog.setCheckOutTime(LocalDateTime.now());
        visitLog = visitLogRepository.save(visitLog);

        // Mark visit request as completed
        VisitRequest visitRequest = visitLog.getVisitRequest();
        visitRequest.setStatus(VisitStatus.COMPLETED);
        visitRequestRepository.save(visitRequest);

        auditLogService.log(null, "CHECK_OUT", "VisitLog",
                visitLog.getId(), "Visitor checked out: " + visitRequest.getVisitor().getName());

        return mapToResponse(visitLog);
    }

    @Override
    public VisitLogResponse getVisitLogById(Long id) {
        VisitLog visitLog = visitLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisitLog", "id", id));
        return mapToResponse(visitLog);
    }

    @Override
    public List<VisitLogResponse> getActiveVisits() {
        return visitLogRepository.findByCheckOutTimeIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitLogResponse> getAllVisitLogs() {
        return visitLogRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps a {@link VisitLog} entity to a {@link VisitLogResponse} DTO.
     *
     * @param vl the visit log entity
     * @return the mapped response DTO
     */
    private VisitLogResponse mapToResponse(VisitLog vl) {
        return VisitLogResponse.builder()
                .id(vl.getId())
                .visitRequestId(vl.getVisitRequest().getId())
                .visitorName(vl.getVisitRequest().getVisitor().getName())
                .associateName(vl.getVisitRequest().getAssociate().getName())
                .purpose(vl.getVisitRequest().getPurpose())
                .checkInTime(vl.getCheckInTime())
                .checkOutTime(vl.getCheckOutTime())
                .securityGuardName(vl.getSecurityGuard() != null ? vl.getSecurityGuard().getName() : null)
                .badgeNumber(vl.getBadgeNumber())
                .build();
    }
}
