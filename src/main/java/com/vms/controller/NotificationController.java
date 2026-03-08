package com.vms.controller;

import com.vms.dto.response.ApiResponse;
import com.vms.entity.Notification;
import com.vms.entity.User;
import com.vms.repository.UserRepository;
import com.vms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user notifications.
 *
 * <p>Provides endpoints for retrieving, filtering, and marking
 * notifications as read. All endpoints require authentication.</p>
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Retrieves all notifications for the currently authenticated user.
     *
     * @param authentication the current user's authentication context
     * @return a {@code 200 OK} response with the list of notifications
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications(
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getNotificationsForUser(user.getId())));
    }

    /**
     * Retrieves only unread notifications for the currently authenticated user.
     *
     * @param authentication the current user's authentication context
     * @return a {@code 200 OK} response with the list of unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications(
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadNotifications(user.getId())));
    }

    /**
     * Returns the count of unread notifications for the currently authenticated user.
     *
     * @param authentication the current user's authentication context
     * @return a {@code 200 OK} response with the unread count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadCount(user.getId())));
    }

    /**
     * Marks a specific notification as read.
     *
     * @param id the notification's ID
     * @return a {@code 200 OK} response confirming the operation
     */
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    /**
     * Resolves the currently authenticated user from the security context.
     *
     * @param authentication the current authentication
     * @return the authenticated {@link User} entity
     * @throws RuntimeException if the user is not found
     */
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
