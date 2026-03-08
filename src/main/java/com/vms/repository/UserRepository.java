package com.vms.repository;

import com.vms.entity.User;
import com.vms.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entity CRUD and query operations.
 *
 * <p>Provides methods to look up users by email, role, department, and name.
 * Email is used as the unique login identifier.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the user's email
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email the email to check
     * @return {@code true} if a user with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves all users with the specified role.
     *
     * @param role the role to filter by
     * @return a list of matching users
     */
    List<User> findByRole(Role role);

    /**
     * Retrieves all users belonging to the specified department.
     *
     * @param department the department name
     * @return a list of matching users
     */
    List<User> findByDepartment(String department);

    /**
     * Searches for users whose name contains the given string (case-insensitive).
     *
     * @param name the search term
     * @return a list of matching users
     */
    List<User> findByNameContainingIgnoreCase(String name);
}
