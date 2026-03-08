package com.vms.service.impl;

import com.vms.dto.response.UserResponse;
import com.vms.entity.User;
import com.vms.enums.Role;
import com.vms.exception.ResourceNotFoundException;
import com.vms.repository.UserRepository;
import com.vms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserService} providing read-only user management operations.
 *
 * <p>Retrieves user data from the repository and maps entities to
 * {@link UserResponse} DTOs for API consumption.</p>
 *
 * @see UserService
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps a {@link User} entity to a {@link UserResponse} DTO.
     *
     * @param user the user entity
     * @return the mapped response DTO
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .department(user.getDepartment())
                .designation(user.getDesignation())
                .role(user.getRole())
                .build();
    }
}
