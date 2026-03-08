package com.vms.repository;

import com.vms.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link VisitLog} entity CRUD and query operations.
 *
 * <p>Provides methods to look up visit logs by visit request, find active
 * (un-checked-out) visits, query by security guard, and retrieve peak
 * visiting hour statistics.</p>
 */
@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    /**
     * Finds the visit log entry associated with a specific visit request.
     *
     * @param visitRequestId the visit request's ID
     * @return an {@link Optional} containing the visit log if found
     */
    Optional<VisitLog> findByVisitRequestId(Long visitRequestId);

    /**
     * Retrieves all visit logs where the visitor has not yet checked out.
     *
     * @return a list of active (un-checked-out) visit logs
     */
    List<VisitLog> findByCheckOutTimeIsNull();

    /**
     * Retrieves all visit logs processed by a specific security guard.
     *
     * @param securityGuardId the security guard's user ID
     * @return a list of visit logs for the given guard
     */
    List<VisitLog> findBySecurityGuardId(Long securityGuardId);

    /**
     * Finds visit logs with check-in times within the specified range.
     *
     * @param start the start of the time range (inclusive)
     * @param end   the end of the time range (inclusive)
     * @return a list of matching visit logs
     */
    @Query("SELECT vl FROM VisitLog vl WHERE vl.checkInTime BETWEEN :start AND :end")
    List<VisitLog> findByCheckInTimeBetween(@Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Returns peak visiting hours within a time range, ranked by visit count (descending).
     *
     * @param start the start of the time range
     * @param end   the end of the time range
     * @return a list of {@code Object[]} arrays where index 0 is the hour (0-23) and index 1 is the count
     */
    @Query("SELECT EXTRACT(HOUR FROM vl.checkInTime), COUNT(vl) FROM VisitLog vl " +
            "WHERE vl.checkInTime BETWEEN :start AND :end " +
            "GROUP BY EXTRACT(HOUR FROM vl.checkInTime) " +
            "ORDER BY COUNT(vl) DESC")
    List<Object[]> findPeakVisitingHours(@Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
