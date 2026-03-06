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

@Repository
public interface VisitRequestRepository extends JpaRepository<VisitRequest, Long> {

    List<VisitRequest> findByStatus(VisitStatus status);

    List<VisitRequest> findByAssociateId(Long associateId);

    List<VisitRequest> findByVisitorId(Long visitorId);

    Optional<VisitRequest> findByQrCodeToken(String qrCodeToken);

    @Query("SELECT vr FROM VisitRequest vr WHERE vr.fromDate = :date")
    List<VisitRequest> findByDate(@Param("date") LocalDate date);

    @Query("SELECT vr FROM VisitRequest vr WHERE vr.fromDate BETWEEN :startDate AND :endDate")
    List<VisitRequest> findByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT vr FROM VisitRequest vr WHERE vr.associate.department = :department")
    List<VisitRequest> findByDepartment(@Param("department") String department);

    @Query("SELECT vr FROM VisitRequest vr WHERE vr.status = :status AND vr.fromDate = :date")
    List<VisitRequest> findByStatusAndDate(@Param("status") VisitStatus status,
            @Param("date") LocalDate date);

    long countByStatus(VisitStatus status);

    @Query("SELECT COUNT(vr) FROM VisitRequest vr WHERE vr.fromDate = :date")
    long countByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(vr) FROM VisitRequest vr WHERE vr.fromDate BETWEEN :startDate AND :endDate")
    long countByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(vr) FROM VisitRequest vr WHERE vr.status = :status AND vr.fromDate = :date")
    long countByStatusAndDate(@Param("status") VisitStatus status, @Param("date") LocalDate date);

    @Query("SELECT COUNT(vr) FROM VisitRequest vr WHERE vr.status = :status AND vr.fromDate BETWEEN :startDate AND :endDate")
    long countByStatusAndDateRange(@Param("status") VisitStatus status,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT vr.fromDate, COUNT(vr) FROM VisitRequest vr WHERE vr.fromDate BETWEEN :startDate AND :endDate GROUP BY vr.fromDate ORDER BY vr.fromDate")
    List<Object[]> countPerDay(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT vr.associate.department, COUNT(vr) FROM VisitRequest vr " +
            "WHERE vr.status = 'APPROVED' GROUP BY vr.associate.department")
    List<Object[]> countByDepartment();

    @Query("SELECT vr.associate.name, COUNT(vr) FROM VisitRequest vr " +
            "WHERE vr.status = 'APPROVED' GROUP BY vr.associate.id, vr.associate.name " +
            "ORDER BY COUNT(vr) DESC")
    List<Object[]> findMostVisitedAssociates();

    List<VisitRequest> findByGroupId(String groupId);

    @Query("SELECT vr FROM VisitRequest vr WHERE vr.status = 'APPROVED' " +
            "AND vr.toDate < :date")
    List<VisitRequest> findExpiredApprovedRequests(@Param("date") LocalDate date);
}
