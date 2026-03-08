package com.vms.service.impl;

import com.vms.entity.Notification;
import com.vms.entity.User;
import com.vms.enums.NotificationType;
import com.vms.exception.ResourceNotFoundException;
import com.vms.repository.NotificationRepository;
import com.vms.repository.UserRepository;
import com.vms.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implementation of {@link NotificationService} providing in-app and email notifications.
 *
 * <p>In-app notifications are persisted to the database. Email notifications are sent
 * via Spring's {@link JavaMailSender}; SMTP failures are logged but do not prevent
 * the operation from succeeding.</p>
 *
 * @see NotificationService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Override
    public void sendNotification(Long recipientId, String title, String message) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", recipientId));

        // Save in-app notification
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(NotificationType.IN_APP)
                .read(false)
                .build();

        notificationRepository.save(notification);

        // Also try to send email (non-blocking, just log errors)
        try {
            sendEmailNotification(recipient.getEmail(), title, message);
        } catch (Exception e) {
            log.warn("Failed to send email notification to {}: {}", recipient.getEmail(), e.getMessage());
        }
    }

    @Override
    public void sendEmailNotification(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setSubject("[VMS] " + subject);
            mailMessage.setText(body);
            mailSender.send(mailMessage);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.warn("Email sending failed (SMTP may not be configured): {}", e.getMessage());
        }
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    public void sendEmailWithQrCode(String toEmail, String visitorName, String qrToken) {
        try {
            log.info("Preparing QR email for {} (Visitor: {})", toEmail, visitorName);

            String subject = "Your Visit is Approved - QR Code Attached";
            String body = String.format(
                    "Dear %s,\n\n" +
                            "Your visit request has been approved. Please use the following QR token for check-in at the security gate:\n\n"
                            +
                            "TOKEN: %s\n\n" +
                            "You can also show this email to the security guard.\n\n" +
                            "Regards,\n" +
                            "VMS Intellect Team",
                    visitorName, qrToken);

            sendEmailNotification(toEmail, subject, body);
        } catch (Exception e) {
            log.error("Failed to send QR email: {}", e.getMessage());
        }
    }

    @Override
    public void sendApprovalEmailWithAccessCard(String toEmail, String visitorName, String visitorEmail,
            String photoPath, String qrToken, String purpose, String fromDate, String toDate,
            String associateName) {
        try {
            log.info("Preparing approval email with access card for {} ({})", visitorName, toEmail);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[VMS] Visit Approved - Your Digital Access Card");

            boolean hasPhoto = photoPath != null && Files.exists(Paths.get(photoPath));

            String html = buildAccessCardHtml(visitorName, visitorEmail, qrToken, purpose,
                    fromDate, toDate, associateName, hasPhoto);

            helper.setText(html, true);

            // Attach visitor photo as inline image
            if (hasPhoto) {
                FileSystemResource photoResource = new FileSystemResource(new File(photoPath));
                helper.addInline("visitorPhoto", photoResource);
            }

            mailSender.send(mimeMessage);
            log.info("Approval email with access card sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send approval email with access card: {}", e.getMessage());
            // Fallback to simple email
            sendEmailWithQrCode(toEmail, visitorName, qrToken);
        }
    }

    /**
     * Builds the HTML content for the digital access card email.
     *
     * @param visitorName   the visitor's display name
     * @param visitorEmail  the visitor's email address
     * @param qrToken       the QR code access token
     * @param purpose       the purpose of the visit
     * @param fromDate      the visit start date
     * @param toDate        the visit end date
     * @param associateName the host associate's name
     * @param hasPhoto      whether the visitor has an uploaded photo
     * @return the complete HTML string for the email body
     */
    private String buildAccessCardHtml(String visitorName, String visitorEmail, String qrToken,
            String purpose, String fromDate, String toDate, String associateName, boolean hasPhoto) {
        String photoSection = hasPhoto
                ? "<img src='cid:visitorPhoto' alt='Visitor Photo' style='width:100px;height:100px;border-radius:50%;object-fit:cover;border:3px solid #0ea5e9;margin-bottom:12px;' />"
                : "<div style='width:100px;height:100px;border-radius:50%;background:linear-gradient(135deg,#0ea5e9,#6366f1);display:flex;align-items:center;justify-content:center;margin:0 auto 12px;font-size:36px;color:white;font-weight:bold;'>" +
                  visitorName.substring(0, 1).toUpperCase() + "</div>";

        String dateDisplay = fromDate.equals(toDate) ? fromDate : fromDate + " to " + toDate;

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='margin:0;padding:0;background-color:#f1f5f9;font-family:Arial,Helvetica,sans-serif;'>" +
                "<div style='max-width:600px;margin:0 auto;padding:40px 20px;'>" +
                // Header
                "<div style='text-align:center;margin-bottom:32px;'>" +
                "<h1 style='color:#0f172a;font-size:24px;margin:0;'>VMS <span style='font-weight:300;font-style:italic;'>Intellect</span></h1>" +
                "</div>" +
                // Greeting
                "<div style='background:white;border-radius:20px;padding:32px;margin-bottom:24px;box-shadow:0 1px 3px rgba(0,0,0,0.1);'>" +
                "<h2 style='color:#0f172a;font-size:20px;margin:0 0 8px;'>Visit Approved! &#10003;</h2>" +
                "<p style='color:#64748b;margin:0;font-size:14px;'>Your visit request has been approved. Please present this access card at the security gate.</p>" +
                "</div>" +
                // Access Card
                "<div style='background:linear-gradient(135deg,#0f172a 0%,#1e293b 100%);border-radius:24px;padding:32px;color:white;margin-bottom:24px;box-shadow:0 20px 40px rgba(15,23,42,0.3);'>" +
                "<div style='text-align:center;margin-bottom:20px;'>" +
                "<p style='font-size:10px;letter-spacing:3px;text-transform:uppercase;color:#94a3b8;margin:0 0 16px;font-weight:bold;'>Digital Access Card</p>" +
                photoSection +
                "<h3 style='margin:0 0 4px;font-size:22px;font-weight:bold;'>" + visitorName + "</h3>" +
                "<p style='margin:0;color:#94a3b8;font-size:13px;'>" + visitorEmail + "</p>" +
                "</div>" +
                "<div style='border-top:1px solid #334155;padding-top:16px;margin-top:16px;'>" +
                "<table style='width:100%;' cellpadding='0' cellspacing='0'>" +
                "<tr>" +
                "<td style='padding:8px 0;'>" +
                "<p style='margin:0;font-size:10px;letter-spacing:2px;text-transform:uppercase;color:#64748b;font-weight:bold;'>Purpose</p>" +
                "<p style='margin:4px 0 0;font-size:14px;color:white;'>" + purpose + "</p>" +
                "</td>" +
                "<td style='padding:8px 0;text-align:right;'>" +
                "<p style='margin:0;font-size:10px;letter-spacing:2px;text-transform:uppercase;color:#64748b;font-weight:bold;'>Date</p>" +
                "<p style='margin:4px 0 0;font-size:14px;color:white;'>" + dateDisplay + "</p>" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding:8px 0;' colspan='2'>" +
                "<p style='margin:0;font-size:10px;letter-spacing:2px;text-transform:uppercase;color:#64748b;font-weight:bold;'>Host</p>" +
                "<p style='margin:4px 0 0;font-size:14px;color:white;'>" + associateName + "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</div>" +
                "<div style='background:#1e293b;border-radius:12px;padding:16px;margin-top:16px;text-align:center;'>" +
                "<p style='margin:0 0 4px;font-size:10px;letter-spacing:2px;text-transform:uppercase;color:#64748b;font-weight:bold;'>Access Token</p>" +
                "<p style='margin:0;font-size:16px;color:#0ea5e9;font-weight:bold;letter-spacing:1px;word-break:break-all;'>" + qrToken + "</p>" +
                "</div>" +
                "</div>" +
                // Instructions
                "<div style='background:white;border-radius:20px;padding:24px;margin-bottom:24px;box-shadow:0 1px 3px rgba(0,0,0,0.1);'>" +
                "<h4 style='color:#0f172a;font-size:14px;margin:0 0 12px;'>Instructions:</h4>" +
                "<ol style='color:#475569;font-size:13px;margin:0;padding-left:20px;line-height:1.8;'>" +
                "<li>Show this email at the security gate upon arrival</li>" +
                "<li>Carry a valid government-issued photo ID</li>" +
                "<li>Your host will be notified of your arrival</li>" +
                "</ol>" +
                "</div>" +
                // Footer
                "<p style='text-align:center;color:#94a3b8;font-size:12px;margin:0;'>" +
                "This is an automated message from VMS Intellect. Please do not reply." +
                "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
