package com.vms.repository;

import com.vms.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Notification} entity CRUD and query operations.
 *
 * <p>Provides methods to retrieve notifications for a specific user,
 * filter by read/unread status, and count unread notifications.</p>
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Retrieves all notifications for a recipient, ordered by creation date (newest first).
     *
     * @param recipientId the ID of the recipient user
     * @return a list of notifications ordered by date descending
     */
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    /**
     * Retrieves all unread notifications for a recipient, ordered by creation date (newest first).
     *
     * @param recipientId the ID of the recipient user
     * @return a list of unread notifications ordered by date descending
     */
    List<Notification> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(Long recipientId);

    /**
     * Counts the number of unread notifications for a recipient.
     *
     * @param recipientId the ID of the recipient user
     * @return the count of unread notifications
     */
    long countByRecipientIdAndReadFalse(Long recipientId);
}
