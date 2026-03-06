package com.vms.controller;

import com.vms.dto.request.ApproveRejectRequest;
import com.vms.dto.request.CreateVisitRequestDTO;
import com.vms.dto.response.ApiResponse;
import com.vms.dto.response.VisitRequestResponse;
import com.vms.entity.User;
import com.vms.enums.VisitStatus;
import com.vms.repository.UserRepository;
import com.vms.service.VisitRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/visit-requests")
@RequiredArgsConstructor
public class VisitRequestController {

    private final VisitRequestService visitRequestService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<VisitRequestResponse>> createVisitRequest(
            @Valid @RequestBody CreateVisitRequestDTO request) {
        VisitRequestResponse response = visitRequestService.createVisitRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Visit request created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getAllVisitRequests() {
        return ResponseEntity.ok(ApiResponse.success(visitRequestService.getAllVisitRequests()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitRequestResponse>> getVisitRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(visitRequestService.getVisitRequestById(id)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByStatus(
            @PathVariable VisitStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByStatus(status)));
    }

    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByDate(date)));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByDateRange(from, to)));
    }

    @GetMapping("/associate/{associateId}")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByAssociate(
            @PathVariable Long associateId) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByAssociate(associateId)));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByDepartment(
            @PathVariable String department) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByDepartment(department)));
    }

    @PutMapping("/{id}/approve-reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSOCIATE')")
    public ResponseEntity<ApiResponse<VisitRequestResponse>> approveOrReject(
            @PathVariable Long id,
            @Valid @RequestBody ApproveRejectRequest request,
            Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        VisitRequestResponse response = visitRequestService.approveOrReject(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Visit request updated", response));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<VisitRequestResponse>> cancelVisitRequest(@PathVariable Long id) {
        VisitRequestResponse response = visitRequestService.cancelVisitRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Visit request cancelled", response));
    }

    @GetMapping("/{id}/qr-code")
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long id) {
        byte[] qrCode = visitRequestService.getQRCode(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }
}
