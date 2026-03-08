package com.vms.enums;

/**
 * Defines the delivery channel for a {@link com.vms.entity.Notification}.
 */
public enum NotificationType {

    /** Notification delivered via email (SMTP). */
    EMAIL,

    /** Notification displayed within the application UI. */
    IN_APP
}
