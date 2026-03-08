package com.vms.enums;

/**
 * Defines the roles available within the Visitor Management System.
 *
 * <p>Each user is assigned exactly one role, which determines their
 * permissions and accessible features.</p>
 *
 * <ul>
 *   <li>{@link #ADMIN} — full system access including dashboards, user management, and approvals</li>
 *   <li>{@link #ASSOCIATE} — can create visit requests and receive visitor notifications</li>
 *   <li>{@link #SECURITY} — handles visitor check-in and check-out at the gate</li>
 * </ul>
 */
public enum Role {

    /** System administrator with full access. */
    ADMIN,

    /** Employee who hosts visitors and creates visit requests. */
    ASSOCIATE,

    /** Security guard who processes visitor check-in/check-out. */
    SECURITY
}
