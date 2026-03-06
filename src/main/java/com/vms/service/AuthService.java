package com.vms.service;

import com.vms.dto.request.LoginRequest;
import com.vms.dto.request.RegisterRequest;
import com.vms.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}
