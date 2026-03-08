package com.vms.service;

import com.vms.dto.response.UserResponse;
import com.vms.enums.Role;

import java.util.List;

/**
 * Service interface for user management operations.
 *
 * <p>Provides read-only access to user profiles with filtering capabilities
 * by role and department.</p>
 */
public interface UserService {

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the user's ID
     * @return the user's profile as a {@link UserResponse}
     * @throws com.vms.exception.ResourceNotFoundException if the user does not exist
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all user profiles
     */
    List<UserResponse> getAllUsers();

    /**
     * Retrieves all users with the specified role.
     *
     * @param role the role to filter by
     * @return a list of matching user profiles
     */
    List<UserResponse> getUsersByRole(Role role);

    /**
     * Retrieves all users belonging to the specified department.
     *
     * @param department the department name
     * @return a list of matching user profiles
     */
    List<UserResponse> getUsersByDepartment(String department);
}
