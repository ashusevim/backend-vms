package com.vms.service;

import com.vms.entity.Notification;

import java.util.List;

public interface NotificationService {
    void sendNotification(Long recipientId, String title, String message);

    void sendEmailNotification(String toEmail, String subject, String body);

    List<Notification> getNotificationsForUser(Long userId);

    List<Notification> getUnreadNotifications(Long userId);

    void markAsRead(Long notificationId);

    long getUnreadCount(Long userId);

    void sendEmailWithQrCode(String toEmail, String visitorName, String qrToken);

    void sendApprovalEmailWithAccessCard(String toEmail, String visitorName, String visitorEmail,
            String photoPath, String qrToken, String purpose, String fromDate, String toDate,
            String associateName);
}
