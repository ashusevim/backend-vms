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

@RestController
@RequestMapping("/api/visit-logs")
@RequiredArgsConstructor
public class VisitLogController {

    private final VisitLogService visitLogService;
    private final UserRepository userRepository;

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

    @PutMapping("/check-out/{id}")
    @PreAuthorize("hasAnyRole('SECURITY', 'ADMIN')")
    public ResponseEntity<ApiResponse<VisitLogResponse>> checkOut(@PathVariable Long id) {
        VisitLogResponse response = visitLogService.checkOut(id);
        return ResponseEntity.ok(ApiResponse.success("Visitor checked out", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitLogResponse>> getVisitLogById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(visitLogService.getVisitLogById(id)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VisitLogResponse>>> getActiveVisits() {
        return ResponseEntity.ok(ApiResponse.success(visitLogService.getActiveVisits()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VisitLogResponse>>> getAllVisitLogs() {
        return ResponseEntity.ok(ApiResponse.success(visitLogService.getAllVisitLogs()));
    }
}
