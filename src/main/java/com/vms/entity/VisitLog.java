package com.vms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing a physical check-in/check-out record for a visit.
 *
 * <p>A visit log is created when a security guard checks in a visitor with
 * an approved {@link VisitRequest}. The log tracks the check-in time,
 * check-out time, badge number, and the security guard who processed the visit.</p>
 *
 * <p>A {@code null} {@code checkOutTime} indicates the visitor is still on premises.</p>
 *
 * @see VisitRequest
 * @see User
 */
@Entity
@Table(name = "visit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visit_log_seq")
    @SequenceGenerator(name = "visit_log_seq", sequenceName = "VISIT_LOG_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_request_id", nullable = false)
    private VisitRequest visitRequest;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_guard_id")
    private User securityGuard;

    @Column(name = "badge_number")
    private String badgeNumber;
}
