package com.vms.config;

import com.vms.entity.VisitLog;
import com.vms.entity.VisitRequest;
import com.vms.enums.VisitStatus;
import com.vms.repository.VisitLogRepository;
import com.vms.repository.VisitRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task to auto-close visits where visitors forgot to check out.
 * Runs daily at 9 PM (after typical office hours).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VisitAutoCloseScheduler {

    private final VisitLogRepository visitLogRepository;
    private final VisitRequestRepository visitRequestRepository;

    @Scheduled(cron = "0 0 21 * * *") // Every day at 9 PM
    @Transactional
    public void autoCloseExpiredVisits() {
        log.info("Running auto-close for expired visits...");

        // Close unchecked-out visits
        List<VisitLog> unclosedVisits = visitLogRepository.findByCheckOutTimeIsNull();
        int closedCount = 0;

        for (VisitLog visitLog : unclosedVisits) {
            VisitRequest request = visitLog.getVisitRequest();
            if (request.getToDate().isBefore(LocalDate.now()) ||
                    request.getToDate().isEqual(LocalDate.now())) {
                visitLog.setCheckOutTime(LocalDateTime.now());
                visitLogRepository.save(visitLog);

                request.setStatus(VisitStatus.COMPLETED);
                visitRequestRepository.save(request);
                closedCount++;
            }
        }

        // Also expire approved requests that were never checked in
        List<VisitRequest> expiredRequests = visitRequestRepository
                .findExpiredApprovedRequests(LocalDate.now());
        for (VisitRequest request : expiredRequests) {
            request.setStatus(VisitStatus.COMPLETED);
            visitRequestRepository.save(request);
        }

        log.info("Auto-closed {} visits, expired {} unchecked requests",
                closedCount, expiredRequests.size());
    }
}
