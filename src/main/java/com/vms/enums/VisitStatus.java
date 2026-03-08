package com.vms.enums;

/**
 * Represents the lifecycle status of a {@link com.vms.entity.VisitRequest}.
 *
 * <p>A visit request transitions through these states:</p>
 * <pre>
 *   PENDING → APPROVED → COMPLETED
 *   PENDING → REJECTED
 *   PENDING → CANCELLED
 *   APPROVED → CANCELLED
 * </pre>
 */
public enum VisitStatus {

    /** Visit request is awaiting approval from an admin or associate. */
    PENDING,

    /** Visit request has been approved; QR code is generated for check-in. */
    APPROVED,

    /** Visit request has been rejected by the approver. */
    REJECTED,

    /** Visit request has been cancelled before completion. */
    CANCELLED,

    /** Visitor has checked out or the visit has expired. */
    COMPLETED
}
