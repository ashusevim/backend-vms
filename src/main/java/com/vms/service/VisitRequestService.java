package com.vms.service;

import com.vms.dto.request.ApproveRejectRequest;
import com.vms.dto.request.CreateVisitRequestDTO;
import com.vms.dto.response.VisitRequestResponse;
import com.vms.enums.VisitStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for visit request lifecycle management.
 *
 * <p>Covers the full lifecycle of a visit request: creation (including group visits),
 * retrieval with various filters, approval/rejection workflows, cancellation,
 * and QR code generation.</p>
 */
public interface VisitRequestService {

    /**
     * Creates a new visit request, optionally with additional visitors for group visits.
     *
     * @param request the visit request creation DTO
     * @return the created visit request details
     * @throws com.vms.exception.BadRequestException       if date validation fails
     * @throws com.vms.exception.ResourceNotFoundException if the visitor or associate is not found
     */
    VisitRequestResponse createVisitRequest(CreateVisitRequestDTO request);

    /**
     * Retrieves a visit request by its ID.
     *
     * @param id the visit request's ID
     * @return the visit request details
     * @throws com.vms.exception.ResourceNotFoundException if the visit request is not found
     */
    VisitRequestResponse getVisitRequestById(Long id);

    /**
     * Retrieves all visit requests in the system.
     *
     * @return a list of all visit request details
     */
    List<VisitRequestResponse> getAllVisitRequests();

    /**
     * Retrieves visit requests filtered by status.
     *
     * @param status the visit status to filter by
     * @return a list of matching visit request details
     */
    List<VisitRequestResponse> getVisitRequestsByStatus(VisitStatus status);

    /**
     * Retrieves visit requests scheduled for a specific date.
     *
     * @param date the date to query
     * @return a list of matching visit request details
     */
    List<VisitRequestResponse> getVisitRequestsByDate(LocalDate date);

    /**
     * Retrieves visit requests within a date range.
     *
     * @param from the start date (inclusive)
     * @param to   the end date (inclusive)
     * @return a list of matching visit request details
     */
    List<VisitRequestResponse> getVisitRequestsByDateRange(LocalDate from, LocalDate to);

    /**
     * Retrieves visit requests created by a specific associate.
     *
     * @param associateId the associate's user ID
     * @return a list of matching visit request details
     */
    List<VisitRequestResponse> getVisitRequestsByAssociate(Long associateId);

    /**
     * Retrieves visit requests for associates in a specific department.
     *
     * @param department the department name
     * @return a list of matching visit request details
     */
    List<VisitRequestResponse> getVisitRequestsByDepartment(String department);

    /**
     * Approves or rejects a pending visit request.
     *
     * <p>If approved, generates a QR code token and sends an approval email.
     * For group visits, all members are approved/rejected together.
     * If rejected, sends a rejection notification to the associate.</p>
     *
     * @param id               the visit request's ID
     * @param request          the approval/rejection details
     * @param approvedByUserId the ID of the user performing the action
     * @return the updated visit request details
     * @throws com.vms.exception.BadRequestException       if the request is not in PENDING status
     * @throws com.vms.exception.ResourceNotFoundException if the visit request or user is not found
     */
    VisitRequestResponse approveOrReject(Long id, ApproveRejectRequest request, Long approvedByUserId);

    /**
     * Cancels a visit request.
     *
     * @param id the visit request's ID
     * @return the updated visit request details
     * @throws com.vms.exception.BadRequestException       if the request is already completed
     * @throws com.vms.exception.ResourceNotFoundException if the visit request is not found
     */
    VisitRequestResponse cancelVisitRequest(Long id);

    /**
     * Generates a QR code image (PNG) for an approved visit request.
     *
     * @param id the visit request's ID
     * @return the QR code image as a byte array
     * @throws com.vms.exception.BadRequestException       if the request has no QR token
     * @throws com.vms.exception.ResourceNotFoundException if the visit request is not found
     */
    byte[] getQRCode(Long id);
}
