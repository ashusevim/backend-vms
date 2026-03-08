package com.vms.repository;

import com.vms.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for {@link AuditLog} entity CRUD and query operations.
 *
 * <p>Provides methods to query audit trail entries by user, action type,
 * related entity, and time range for compliance and reporting purposes.</p>
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Retrieves all audit log entries for a specific user.
     *
     * @param userId the ID of the user whose actions to retrieve
     * @return a list of audit log entries
     */
    List<AuditLog> findByUserId(Long userId);

    /**
     * Retrieves all audit log entries for a specific action type.
     *
     * @param action the action string (e.g., "LOGIN", "CHECK_IN")
     * @return a list of matching audit log entries
     */
    List<AuditLog> findByAction(String action);

    /**
     * Retrieves audit log entries related to a specific entity.
     *
     * @param entityType the entity type (e.g., "VisitRequest", "User")
     * @param entityId   the entity's ID
     * @return a list of matching audit log entries
     */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    /**
     * Retrieves audit log entries within a specific time range.
     *
     * @param start the start of the time range (inclusive)
     * @param end   the end of the time range (inclusive)
     * @return a list of matching audit log entries
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
