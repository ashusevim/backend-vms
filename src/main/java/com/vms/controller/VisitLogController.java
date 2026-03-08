package com.vms.controller;

import com.vms.dto.request.CheckInRequest;
import com.vms.dto.response.ApiResponse;
import com.vms.dto.response.VisitLogResponse;
import com.vms.entity.User;
import com.vms.repository.UserRepository;
import com.vms.service.VisitLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for visitor check-in/check-out and visit log management.
 *
 * <p>Check-in and check-out endpoints are restricted to users with
 * {@code SECURITY} or {@code ADMIN} roles. Read endpoints are available
 * to all authenticated users.</p>
 */
@RestController
@RequestMapping("/api/visit-logs")
@RequiredArgsConstructor
public class VisitLogController {

    private final VisitLogService visitLogService;
    private final UserRepository userRepository;

    /**
     * Checks in a visitor for an approved visit request.
     *
     * @param request        the check-in request details
     * @param authentication the current user's authentication context
     * @return a {@code 201 Created} response with the visit log entry
     */
    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('SECURITY', 'ADMIN')")
    public ResponseEntity<ApiResponse<VisitLogResponse>> checkIn(
            @Valid @RequestBody CheckInRequest request,
            Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        VisitLogResponse response = visitLogService.checkIn(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Visitor checked in", response));
    }

    /**
     * Checks out a visitor and marks the visit as completed.
     *
     * @param id the visit log entry's ID
     * @return a {@code 200 OK} response with the updated visit log entry
     */
    @PutMapping("/check-out/{id}")
    @PreAuthorize("hasAnyRole('SECURITY', 'ADMIN')")
    public ResponseEntity<ApiResponse<VisitLogResponse>> checkOut(@PathVariable Long id) {
        VisitLogResponse response = visitLogService.checkOut(id);
        return ResponseEntity.ok(ApiResponse.success("Visitor checked out", response));
    }

    /**
     * Retrieves a specific visit log entry by its ID.
     *
     * @param id the visit log entry's ID
     * @return a {@code 200 OK} response with the visit log entry
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitLogResponse>> getVisitLogById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(visitLogService.getVisitLogById(id)));
    }

    /**
     * Retrieves all currently active visits (checked in but not checked out).
     *
     * @return a {@code 200 OK} response with the list of active visits
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VisitLogResponse>>> getActiveVisits() {
        return ResponseEntity.ok(ApiResponse.success(visitLogService.getActiveVisits()));
    }

    /**
     * Retrieves all visit log entries.
     *
     * @return a {@code 200 OK} response with the list of all visit logs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VisitLogResponse>>> getAllVisitLogs() {
        return ResponseEntity.ok(ApiResponse.success(visitLogService.getAllVisitLogs()));
    }
}
