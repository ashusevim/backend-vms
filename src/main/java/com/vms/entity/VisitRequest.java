package com.vms.entity;

import com.vms.enums.VisitStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * JPA entity representing a request for a visitor to access the premises.
 *
 * <p>A visit request is created by an associate (or on behalf of one) and goes
 * through the following lifecycle:</p>
 * <ol>
 *   <li>{@link VisitStatus#PENDING} — awaiting admin/associate approval</li>
 *   <li>{@link VisitStatus#APPROVED} — approved, QR code generated for check-in</li>
 *   <li>{@link VisitStatus#REJECTED} — declined by the approver</li>
 *   <li>{@link VisitStatus#CANCELLED} — cancelled before completion</li>
 *   <li>{@link VisitStatus#COMPLETED} — visitor has checked out or visit expired</li>
 * </ol>
 *
 * <p>Group visits share a common {@code groupId} so they can be approved or
 * managed together.</p>
 *
 * @see Visitor
 * @see User
 * @see VisitStatus
 */
@Entity
@Table(name = "visit_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visit_request_seq")
    @SequenceGenerator(name = "visit_request_seq", sequenceName = "VISIT_REQUEST_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id", nullable = false)
    private Visitor visitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associate_id", nullable = false)
    private User associate;

    @Column(nullable = false)
    private String purpose;

    private String category;

    @Column(name = "visit_from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "visit_to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "in_time")
    private LocalTime inTime;

    @Column(name = "out_time")
    private LocalTime outTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus status;

    @Column(name = "expiry_date")
    private LocalDateTime expiry;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    private String remarks;

    @Column(name = "qr_code_token")
    private String qrCodeToken;

    @Column(name = "group_id")
    private String groupId;
}
