package com.vms.service;

import com.vms.entity.Notification;

import java.util.List;

/**
 * Service interface for managing in-app and email notifications.
 *
 * <p>Provides methods to send notifications to users via in-app messages
 * and email, retrieve notification lists, and manage read/unread status.</p>
 */
public interface NotificationService {

    /**
     * Sends an in-app notification to a user and attempts to deliver it via email as well.
     *
     * @param recipientId the ID of the recipient user
     * @param title       the notification title
     * @param message     the notification message body
     */
    void sendNotification(Long recipientId, String title, String message);

    /**
     * Sends a simple email notification.
     *
     * @param toEmail the recipient's email address
     * @param subject the email subject
     * @param body    the email body text
     */
    void sendEmailNotification(String toEmail, String subject, String body);

    /**
     * Retrieves all notifications for a user, ordered by newest first.
     *
     * @param userId the user's ID
     * @return a list of notifications
     */
    List<Notification> getNotificationsForUser(Long userId);

    /**
     * Retrieves only unread notifications for a user, ordered by newest first.
     *
     * @param userId the user's ID
     * @return a list of unread notifications
     */
    List<Notification> getUnreadNotifications(Long userId);

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId the notification's ID
     * @throws com.vms.exception.ResourceNotFoundException if the notification does not exist
     */
    void markAsRead(Long notificationId);

    /**
     * Returns the count of unread notifications for a user.
     *
     * @param userId the user's ID
     * @return the number of unread notifications
     */
    long getUnreadCount(Long userId);

    /**
     * Sends an email containing the QR code token for visit check-in.
     *
     * @param toEmail     the visitor's email address
     * @param visitorName the visitor's display name
     * @param qrToken     the generated QR code token
     */
    void sendEmailWithQrCode(String toEmail, String visitorName, String qrToken);

    /**
     * Sends a styled approval email containing a digital access card with visit details.
     *
     * @param toEmail       the visitor's email address
     * @param visitorName   the visitor's display name
     * @param visitorEmail  the visitor's email (shown on the access card)
     * @param photoPath     the file path to the visitor's photo (may be {@code null})
     * @param qrToken       the generated QR code token
     * @param purpose       the purpose of the visit
     * @param fromDate      the visit start date as a string
     * @param toDate        the visit end date as a string
     * @param associateName the name of the host associate
     */
    void sendApprovalEmailWithAccessCard(String toEmail, String visitorName, String visitorEmail,
            String photoPath, String qrToken, String purpose, String fromDate, String toDate,
            String associateName);
}
