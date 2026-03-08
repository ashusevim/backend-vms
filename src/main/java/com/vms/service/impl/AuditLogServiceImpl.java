package com.vms.service.impl;

import com.vms.entity.AuditLog;
import com.vms.repository.AuditLogRepository;
import com.vms.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AuditLogService} that persists audit log entries to the database.
 *
 * @see AuditLogService
 * @see AuditLog
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(Long userId, String action, String entityType, Long entityId, String details) {
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();

        auditLogRepository.save(auditLog);
    }
}
