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

/**
 * REST controller for visit request lifecycle management.
 *
 * <p>Provides endpoints for creating, retrieving, filtering, approving/rejecting,
 * cancelling visit requests, and generating QR codes. Approval and rejection
 * require {@code ADMIN} or {@code ASSOCIATE} role.</p>
 */
@RestController
@RequestMapping("/api/visit-requests")
@RequiredArgsConstructor
public class VisitRequestController {

    private final VisitRequestService visitRequestService;
    private final UserRepository userRepository;

    /**
     * Creates a new visit request.
     *
     * @param request the visit request creation details
     * @return a {@code 201 Created} response with the created visit request
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VisitRequestResponse>> createVisitRequest(
            @Valid @RequestBody CreateVisitRequestDTO request) {
        VisitRequestResponse response = visitRequestService.createVisitRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Visit request created", response));
    }

    /**
     * Retrieves all visit requests.
     *
     * @return a {@code 200 OK} response with the list of all visit requests
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getAllVisitRequests() {
        return ResponseEntity.ok(ApiResponse.success(visitRequestService.getAllVisitRequests()));
    }

    /**
     * Retrieves a visit request by its ID.
     *
     * @param id the visit request's ID
     * @return a {@code 200 OK} response with the visit request details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitRequestResponse>> getVisitRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(visitRequestService.getVisitRequestById(id)));
    }

    /**
     * Retrieves visit requests filtered by status.
     *
     * @param status the visit status to filter by
     * @return a {@code 200 OK} response with matching visit requests
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByStatus(
            @PathVariable VisitStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByStatus(status)));
    }

    /**
     * Retrieves visit requests for a specific date.
     *
     * @param date the date to query (ISO format)
     * @return a {@code 200 OK} response with matching visit requests
     */
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByDate(date)));
    }

    /**
     * Retrieves visit requests within a date range.
     *
     * @param from the start date (ISO format, inclusive)
     * @param to   the end date (ISO format, inclusive)
     * @return a {@code 200 OK} response with matching visit requests
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByDateRange(from, to)));
    }

    /**
     * Retrieves visit requests created by a specific associate.
     *
     * @param associateId the associate's user ID
     * @return a {@code 200 OK} response with matching visit requests
     */
    @GetMapping("/associate/{associateId}")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByAssociate(
            @PathVariable Long associateId) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByAssociate(associateId)));
    }

    /**
     * Retrieves visit requests for associates in a specific department.
     *
     * @param department the department name
     * @return a {@code 200 OK} response with matching visit requests
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<VisitRequestResponse>>> getByDepartment(
            @PathVariable String department) {
        return ResponseEntity.ok(ApiResponse.success(
                visitRequestService.getVisitRequestsByDepartment(department)));
    }

    /**
     * Approves or rejects a pending visit request.
     *
     * @param id             the visit request's ID
     * @param request        the approval/rejection details
     * @param authentication the current user's authentication context
     * @return a {@code 200 OK} response with the updated visit request
     */
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

    /**
     * Cancels a visit request.
     *
     * @param id the visit request's ID
     * @return a {@code 200 OK} response with the cancelled visit request
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<VisitRequestResponse>> cancelVisitRequest(@PathVariable Long id) {
        VisitRequestResponse response = visitRequestService.cancelVisitRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Visit request cancelled", response));
    }

    /**
     * Generates and returns a QR code image (PNG) for an approved visit request.
     *
     * @param id the visit request's ID
     * @return a {@code 200 OK} response with the QR code image bytes
     */
    @GetMapping("/{id}/qr-code")
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long id) {
        byte[] qrCode = visitRequestService.getQRCode(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }
}
