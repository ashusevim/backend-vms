package com.vms.repository;

import com.vms.entity.VisitRequest;
import com.vms.enums.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link VisitRequest} entity CRUD and query operations.
 *
 * <p>Provides comprehensive query methods for filtering visit requests by status,
 * date, date range, associate, department, and QR code token. Also includes
 * aggregate count queries used by the analytics dashboard.</p>
 *
 * @see VisitRequest
 * @see VisitStatus
 */
@Repository
public interface VisitRequestRepository extends JpaRepository<VisitRequest, Long> {

    /**
     * Finds all visit requests with the given status.
     *
     * @param status the visit status to filter by
     * @return a list of matching visit requests
     */
    List<VisitRequest> findByStatus(VisitStatus status);

    /**
     * Finds all visit requests created by the specified associate.
     *
     * @param associateId the associate's user ID
     * @return a list of matching visit requests
     */
    List<VisitRequest> findByAssociateId(Long associateId);

    /**
     * Finds all visit requests for the specified visitor.
     *
     * @param visitorId the visitor's ID
     * @return a list of matching visit requests
     */
    List<VisitRequest> findByVisitorId(Long visitorId);

    /**
     * Finds a visit request by its unique QR code token.
     *
     * @param qrCodeToken the QR code token assigned upon approval
     * @return an {@link Optional} containing the visit request if found
     */
    Optional<VisitRequest> findByQrCodeToken(String qrCodeToken);

    /**
     * Finds all visit requests scheduled for a specific date.
     *
     * @param date the date to query
     * @return a list of matching visit requests
     */
    @Query("SELECT vr FROM VisitRequest vr WHERE vr.fromDate = :date")
    List<VisitRequest> findByDate(@Param("date") LocalDate date);

    /**
     * Finds all visit requests within the given date range (inclusive).
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return a list of matching visit requests
     */
    @Query("SELECT vr FROM VisitRequest vr WHERE vr.fromDate BETWEEN :startDate AND :endDate")
    List<VisitRequest> findByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Finds all visit requests where the associate belongs to the specified department.
     *
     * @param department the department name
     * @return a list of matching visit requests
     */
    @Query("SELECT vr FROM VisitRequest vr WHERE vr.associate.department = :department")
    List<VisitRequest> findByDepartment(@Param("department") String department);

    /**
     * Finds visit requests matching both the given status and date.
     *
     * @param status the visit status to filter by
     * @param date   the date to query
     * @return a list of matching visit requests
     */
    @Query("SELECT vr FROM VisitRequest vr WHERE vr.status = :status AND vr.fromDate = :date")
    List<VisitRequest> findByStatusAndDate(@Param("status") VisitStatus status,
            @Param("date") LocalDate date);

    /**
     * Counts visit requests with the given status.
     *
     * @param status the status to count
     * @return the total count
     */
    long countByStatus(VisitStatus status);

    /**
     * Counts visit requests scheduled for a specific date.
     *
     * @param date the date to count
     * @return the total count
     */
    @Query("SELECT COUNT(vr) FROM VisitRequest vr WHERE vr.fromDate = :date")
    long countByDate(@Param("date") LocalDate date);

    /**
     * Counts visit requests within the given date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return the total count
     */
    @Query("SELECT COUNT(vr) FROM VisitRequest vr WHERE vr.fromDate BETWEEN :startDate AND :endDate")
    long countByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Counts visit requests matching both the given status and date.
     *
     * @param status the status to filter by
     * @param date   the date to count
     * @return the total count
     */
    @Query("SELECT COUNT(vr) FROM VisitRequest vr WHERE vr.status = :status AND vr.fromDate = :date")
    long countByStatusAndDate(@Param("status") VisitStatus status, @Param("date") LocalDate date);

    /**
     * Counts visit requests matching the given status within a date range.
     *
     * @param status    the status to filter by
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return the total count
     */
    @Query("SELECT COUNT(vr) FROM VisitRequest vr WHERE vr.status = :status AND vr.fromDate BETWEEN :startDate AND :endDate")
    long countByStatusAndDateRange(@Param("status") VisitStatus status,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Returns daily visit request counts within a date range, grouped by date.
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return a list of {@code Object[]} arrays where index 0 is the date and index 1 is the count
     */
    @Query("SELECT vr.fromDate, COUNT(vr) FROM VisitRequest vr WHERE vr.fromDate BETWEEN :startDate AND :endDate GROUP BY vr.fromDate ORDER BY vr.fromDate")
    List<Object[]> countPerDay(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Returns approved visit request counts grouped by department.
     *
     * @return a list of {@code Object[]} arrays where index 0 is the department name and index 1 is the count
     */
    @Query("SELECT vr.associate.department, COUNT(vr) FROM VisitRequest vr " +
            "WHERE vr.status = 'APPROVED' GROUP BY vr.associate.department")
    List<Object[]> countByDepartment();

    /**
     * Returns the most visited associates ranked by approved visit count (descending).
     *
     * @return a list of {@code Object[]} arrays where index 0 is the associate name and index 1 is the count
     */
    @Query("SELECT vr.associate.name, COUNT(vr) FROM VisitRequest vr " +
            "WHERE vr.status = 'APPROVED' GROUP BY vr.associate.id, vr.associate.name " +
            "ORDER BY COUNT(vr) DESC")
    List<Object[]> findMostVisitedAssociates();

    /**
     * Finds all visit requests sharing the same group ID.
     *
     * @param groupId the group identifier for group visits
     * @return a list of visit requests in the group
     */
    List<VisitRequest> findByGroupId(String groupId);

    /**
     * Finds approved visit requests whose to-date has passed (expired).
     *
     * <p>Used by the auto-close scheduler to complete stale approved visits.</p>
     *
     * @param date the reference date (typically today)
     * @return a list of expired approved visit requests
     */
    @Query("SELECT vr FROM VisitRequest vr WHERE vr.status = 'APPROVED' " +
            "AND vr.toDate < :date")
    List<VisitRequest> findExpiredApprovedRequests(@Param("date") LocalDate date);
}
