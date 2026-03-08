package com.vms.service;

import com.vms.dto.request.CheckInRequest;
import com.vms.dto.response.VisitLogResponse;

import java.util.List;

/**
 * Service interface for visitor check-in and check-out operations.
 *
 * <p>Manages the physical visit lifecycle at the security gate, including
 * visitor check-in (via QR code or visit request ID), check-out, and
 * retrieval of active and historical visit logs.</p>
 */
public interface VisitLogService {

    /**
     * Checks in a visitor for an approved visit request.
     *
     * @param request         the check-in request containing the visit request ID or QR token
     * @param securityGuardId the ID of the security guard processing the check-in
     * @return the created visit log entry
     * @throws com.vms.exception.BadRequestException        if the visit is not approved or already checked in
     * @throws com.vms.exception.ResourceNotFoundException  if the visit request is not found
     */
    VisitLogResponse checkIn(CheckInRequest request, Long securityGuardId);

    /**
     * Checks out a visitor and marks the visit as completed.
     *
     * @param visitLogId the visit log entry's ID
     * @return the updated visit log entry
     * @throws com.vms.exception.BadRequestException        if the visitor is already checked out
     * @throws com.vms.exception.ResourceNotFoundException  if the visit log is not found
     */
    VisitLogResponse checkOut(Long visitLogId);

    /**
     * Retrieves a specific visit log entry by its ID.
     *
     * @param id the visit log entry's ID
     * @return the visit log entry
     * @throws com.vms.exception.ResourceNotFoundException if the visit log is not found
     */
    VisitLogResponse getVisitLogById(Long id);

    /**
     * Retrieves all currently active visits (checked in but not yet checked out).
     *
     * @return a list of active visit log entries
     */
    List<VisitLogResponse> getActiveVisits();

    /**
     * Retrieves all visit log entries.
     *
     * @return a list of all visit log entries
     */
    List<VisitLogResponse> getAllVisitLogs();
}
