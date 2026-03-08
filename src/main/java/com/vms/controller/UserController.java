package com.vms.controller;

import com.vms.dto.response.ApiResponse;
import com.vms.dto.response.UserResponse;
import com.vms.enums.Role;
import com.vms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management endpoints.
 *
 * <p>Provides endpoints to retrieve user profiles with filtering by role
 * and department. Admin-only endpoints require the {@code ADMIN} role.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves all users. Restricted to admins.
     *
     * @return a {@code 200 OK} response with the list of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the user's ID
     * @return a {@code 200 OK} response with the user profile
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    /**
     * Retrieves all users with a specific role. Restricted to admins.
     *
     * @param role the role to filter by
     * @return a {@code 200 OK} response with matching users
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUsersByRole(role)));
    }

    /**
     * Retrieves all users belonging to a specific department.
     *
     * @param department the department name
     * @return a {@code 200 OK} response with matching users
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByDepartment(
            @PathVariable String department) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUsersByDepartment(department)));
    }
}
