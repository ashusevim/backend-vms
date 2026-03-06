package com.vms.service;

import com.vms.dto.response.UserResponse;
import com.vms.enums.Role;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersByRole(Role role);

    List<UserResponse> getUsersByDepartment(String department);
}
