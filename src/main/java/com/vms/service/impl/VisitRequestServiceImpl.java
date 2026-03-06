package com.vms.service.impl;

import com.vms.dto.request.ApproveRejectRequest;
import com.vms.dto.request.CreateVisitRequestDTO;
import com.vms.dto.response.VisitRequestResponse;
import com.vms.entity.User;
import com.vms.entity.VisitRequest;
import com.vms.entity.Visitor;
import com.vms.enums.VisitStatus;
import com.vms.exception.BadRequestException;
import com.vms.exception.ResourceNotFoundException;
import com.vms.repository.UserRepository;
import com.vms.repository.VisitRequestRepository;
import com.vms.repository.VisitorRepository;
import com.vms.service.AuditLogService;
import com.vms.service.NotificationService;
import com.vms.service.VisitRequestService;
import com.vms.util.QRCodeGenerator;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitRequestServiceImpl implements VisitRequestService {

    private final VisitRequestRepository visitRequestRepository;
    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;
    private final QRCodeGenerator qrCodeGenerator;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public VisitRequestResponse createVisitRequest(CreateVisitRequestDTO request) {
        // Validate dates
        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new BadRequestException("From date must be before or equal to To date");
        }

        // Get or create visitor
        Visitor visitor;
        if (request.getVisitorId() != null) {
            visitor = visitorRepository.findById(request.getVisitorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Visitor", "id", request.getVisitorId()));
        } else {
            visitor = Visitor.builder()
                    .name(request.getVisitorName())
                    .mobileNumber(request.getVisitorMobile())
                    .email(request.getVisitorEmail())
                    .companyName(request.getVisitorCompany())
                    .idProofType(request.getVisitorIdProofType())
                    .idProofNumber(request.getVisitorIdProofNumber())
                    .build();
            visitor = visitorRepository.save(visitor);
        }

        // Get associate
        User associate = userRepository.findById(request.getAssociateId())
                .orElseThrow(() -> new ResourceNotFoundException("Associate", "id", request.getAssociateId()));

        // Generate group ID if there are additional visitors
        String groupId = null;
        if (request.getAdditionalVisitors() != null && !request.getAdditionalVisitors().isEmpty()) {
            groupId = UUID.randomUUID().toString();
        }

        // Create main visit request
        VisitRequest visitRequest = buildVisitRequest(request, visitor, associate, groupId);
        visitRequest = visitRequestRepository.save(visitRequest);

        // Create visit requests for additional visitors (group visit)
        if (request.getAdditionalVisitors() != null) {
            for (CreateVisitRequestDTO.VisitorInfo additionalVisitor : request.getAdditionalVisitors()) {
                Visitor addVisitor = Visitor.builder()
                        .name(additionalVisitor.getName())
                        .mobileNumber(additionalVisitor.getMobileNumber())
                        .email(additionalVisitor.getEmail())
                        .companyName(additionalVisitor.getCompanyName())
                        .idProofType(additionalVisitor.getIdProofType())
                        .idProofNumber(additionalVisitor.getIdProofNumber())
                        .build();
                addVisitor = visitorRepository.save(addVisitor);

                VisitRequest addRequest = buildVisitRequest(request, addVisitor, associate, groupId);
                visitRequestRepository.save(addRequest);
            }
        }

        // Notify associate about new visit request
        notificationService.sendNotification(
                associate.getId(),
                "New Visit Request",
                "Visitor " + visitor.getName() + " has requested a visit for " + request.getPurpose());

        auditLogService.log(null, "CREATE_VISIT_REQUEST", "VisitRequest",
                visitRequest.getId(), "Visit request created for visitor: " + visitor.getName());

        return mapToResponse(visitRequest);
    }

    private VisitRequest buildVisitRequest(CreateVisitRequestDTO request, Visitor visitor,
            User associate, String groupId) {
        return VisitRequest.builder()
                .visitor(visitor)
                .associate(associate)
                .purpose(request.getPurpose())
                .category(request.getCategory())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .inTime(request.getInTime())
                .outTime(request.getOutTime())
                .status(VisitStatus.PENDING)
                .groupId(groupId)
                .build();
    }

    @Override
    public VisitRequestResponse getVisitRequestById(Long id) {
        VisitRequest request = visitRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisitRequest", "id", id));
        return mapToResponse(request);
    }

    @Override
    public List<VisitRequestResponse> getAllVisitRequests() {
        return visitRequestRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitRequestResponse> getVisitRequestsByStatus(VisitStatus status) {
        return visitRequestRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitRequestResponse> getVisitRequestsByDate(LocalDate date) {
        return visitRequestRepository.findByDate(date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitRequestResponse> getVisitRequestsByDateRange(LocalDate from, LocalDate to) {
        return visitRequestRepository.findByDateRange(from, to).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitRequestResponse> getVisitRequestsByAssociate(Long associateId) {
        return visitRequestRepository.findByAssociateId(associateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitRequestResponse> getVisitRequestsByDepartment(String department) {
        return visitRequestRepository.findByDepartment(department).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VisitRequestResponse approveOrReject(Long id, ApproveRejectRequest request, Long approvedByUserId) {
        VisitRequest visitRequest = visitRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisitRequest", "id", id));

        if (visitRequest.getStatus() != VisitStatus.PENDING) {
            throw new BadRequestException("Can only approve/reject PENDING requests. Current status: "
                    + visitRequest.getStatus());
        }

        User approvedBy = userRepository.findById(approvedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", approvedByUserId));

        visitRequest.setStatus(request.getStatus());
        visitRequest.setRemarks(request.getRemarks());
        visitRequest.setApprovedBy(approvedBy);
        visitRequest.setApprovedAt(LocalDateTime.now());

        // If approved, generate QR code token
        if (request.getStatus() == VisitStatus.APPROVED) {
            String qrToken = qrCodeGenerator.generateToken();
            visitRequest.setQrCodeToken(qrToken);

            // Send approval email with access card to the main visitor
            Visitor mainVisitor = visitRequest.getVisitor();
            notificationService.sendApprovalEmailWithAccessCard(
                    mainVisitor.getEmail(),
                    mainVisitor.getName(),
                    mainVisitor.getEmail(),
                    mainVisitor.getPhotoPath(),
                    qrToken,
                    visitRequest.getPurpose(),
                    visitRequest.getFromDate().toString(),
                    visitRequest.getToDate().toString(),
                    visitRequest.getAssociate().getName());

            // If this is a group visit, approve all in the group
            if (visitRequest.getGroupId() != null) {
                List<VisitRequest> groupRequests = visitRequestRepository
                        .findByGroupId(visitRequest.getGroupId());
                for (VisitRequest gr : groupRequests) {
                    if (!gr.getId().equals(id)) {
                        gr.setStatus(VisitStatus.APPROVED);
                        gr.setApprovedBy(approvedBy);
                        gr.setApprovedAt(LocalDateTime.now());
                        String groupQrToken = qrCodeGenerator.generateToken();
                        gr.setQrCodeToken(groupQrToken);
                        gr.setRemarks(request.getRemarks());
                        visitRequestRepository.save(gr);

                        // Send approval email with access card to group visitors
                        Visitor groupVisitor = gr.getVisitor();
                        notificationService.sendApprovalEmailWithAccessCard(
                                groupVisitor.getEmail(),
                                groupVisitor.getName(),
                                groupVisitor.getEmail(),
                                groupVisitor.getPhotoPath(),
                                groupQrToken,
                                gr.getPurpose(),
                                gr.getFromDate().toString(),
                                gr.getToDate().toString(),
                                gr.getAssociate().getName());
                    }
                }
            }
        }

        visitRequest = visitRequestRepository.save(visitRequest);

        // Notify about status change
        String statusText = request.getStatus() == VisitStatus.APPROVED ? "approved" : "rejected";

        // If rejected, send rejection email to the referred associate
        if (request.getStatus() == VisitStatus.REJECTED) {
            User associate = visitRequest.getAssociate();
            String visitorName = visitRequest.getVisitor().getName();
            String remarks = request.getRemarks() != null ? request.getRemarks() : "No reason provided";

            String subject = "Visit Request Rejected - " + visitorName;
            String body = String.format(
                    "Dear %s,\n\n" +
                    "The visit request for visitor \"%s\" has been rejected by the admin.\n\n" +
                    "Details:\n" +
                    "  Visitor: %s\n" +
                    "  Purpose: %s\n" +
                    "  Date: %s to %s\n" +
                    "  Rejection Reason: %s\n\n" +
                    "If you have any questions, please contact the admin.\n\n" +
                    "Regards,\n" +
                    "VMS Intellect Team",
                    associate.getName(),
                    visitorName,
                    visitorName,
                    visitRequest.getPurpose(),
                    visitRequest.getFromDate(),
                    visitRequest.getToDate(),
                    remarks);

            notificationService.sendEmailNotification(associate.getEmail(), subject, body);

            // Also send in-app notification to the associate
            notificationService.sendNotification(
                    associate.getId(),
                    "Visit Request Rejected",
                    "Visit request for " + visitorName + " has been rejected. Reason: " + remarks);
        }

        auditLogService.log(approvedByUserId, "VISIT_" + statusText.toUpperCase(),
                "VisitRequest", id, "Visit request " + statusText);

        return mapToResponse(visitRequest);
    }

    @Override
    @Transactional
    public VisitRequestResponse cancelVisitRequest(Long id) {
        VisitRequest visitRequest = visitRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisitRequest", "id", id));

        if (visitRequest.getStatus() == VisitStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed visit request");
        }

        visitRequest.setStatus(VisitStatus.CANCELLED);
        visitRequest = visitRequestRepository.save(visitRequest);

        auditLogService.log(null, "CANCEL_VISIT_REQUEST", "VisitRequest", id,
                "Visit request cancelled");

        return mapToResponse(visitRequest);
    }

    @Override
    public byte[] getQRCode(Long id) {
        VisitRequest visitRequest = visitRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisitRequest", "id", id));

        if (visitRequest.getQrCodeToken() == null) {
            throw new BadRequestException("QR code is only available for approved requests");
        }

        try {
            // Encode visit details for QR
            String qrContent = String.format("VMS|%d|%s|%s|%s",
                    visitRequest.getId(),
                    visitRequest.getQrCodeToken(),
                    visitRequest.getVisitor().getName(),
                    visitRequest.getAssociate().getName());
            return qrCodeGenerator.generateQRCodeImage(qrContent);
        } catch (WriterException | IOException e) {
            throw new BadRequestException("Failed to generate QR code: " + e.getMessage());
        }
    }

    private VisitRequestResponse mapToResponse(VisitRequest vr) {
        return VisitRequestResponse.builder()
                .id(vr.getId())
                .purpose(vr.getPurpose())
                .category(vr.getCategory())
                .fromDate(vr.getFromDate())
                .toDate(vr.getToDate())
                .inTime(vr.getInTime())
                .outTime(vr.getOutTime())
                .status(vr.getStatus())
                .remarks(vr.getRemarks())
                .qrCodeToken(vr.getQrCodeToken())
                .groupId(vr.getGroupId())
                .createdAt(vr.getCreatedAt())
                .approvedAt(vr.getApprovedAt())
                .visitorId(vr.getVisitor().getId())
                .visitorName(vr.getVisitor().getName())
                .visitorMobile(vr.getVisitor().getMobileNumber())
                .visitorEmail(vr.getVisitor().getEmail())
                .visitorCompany(vr.getVisitor().getCompanyName())
                .associateId(vr.getAssociate().getId())
                .associateName(vr.getAssociate().getName())
                .associateDepartment(vr.getAssociate().getDepartment())
                .approvedByName(vr.getApprovedBy() != null ? vr.getApprovedBy().getName() : null)
                .build();
    }
}
