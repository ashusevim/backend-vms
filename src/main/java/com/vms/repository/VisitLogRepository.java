package com.vms.repository;

import com.vms.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    Optional<VisitLog> findByVisitRequestId(Long visitRequestId);

    List<VisitLog> findByCheckOutTimeIsNull();

    List<VisitLog> findBySecurityGuardId(Long securityGuardId);

    @Query("SELECT vl FROM VisitLog vl WHERE vl.checkInTime BETWEEN :start AND :end")
    List<VisitLog> findByCheckInTimeBetween(@Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT EXTRACT(HOUR FROM vl.checkInTime), COUNT(vl) FROM VisitLog vl " +
            "WHERE vl.checkInTime BETWEEN :start AND :end " +
            "GROUP BY EXTRACT(HOUR FROM vl.checkInTime) " +
            "ORDER BY COUNT(vl) DESC")
    List<Object[]> findPeakVisitingHours(@Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
