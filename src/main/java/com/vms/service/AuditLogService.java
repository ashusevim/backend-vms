package com.vms.service;

/**
 * Service interface for recording audit trail entries.
 *
 * <p>Every significant action in the system (login, registration, visit request
 * creation, approval, check-in, check-out, etc.) should be logged via this service
 * for compliance, traceability, and debugging purposes.</p>
 */
public interface AuditLogService {

    /**
     * Records an audit log entry.
     *
     * @param userId     the ID of the user performing the action (may be {@code null} for system actions)
     * @param action     the action performed (e.g., "LOGIN", "CHECK_IN", "CREATE_VISIT_REQUEST")
     * @param entityType the type of entity affected (e.g., "User", "VisitRequest", "VisitLog")
     * @param entityId   the ID of the affected entity
     * @param details    a human-readable description of the action
     */
    void log(Long userId, String action, String entityType, Long entityId, String details);
}
