package com.vms.service;

import com.vms.dto.request.LoginRequest;
import com.vms.dto.request.RegisterRequest;
import com.vms.dto.response.AuthResponse;

/**
 * Service interface for authentication and user registration operations.
 *
 * <p>Handles credential validation, JWT token generation, and new user account creation.</p>
 */
public interface AuthService {

    /**
     * Authenticates a user with the provided credentials and returns a JWT token.
     *
     * @param request the login request containing email and password
     * @return an {@link AuthResponse} with the JWT token and user profile
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    AuthResponse login(LoginRequest request);

    /**
     * Registers a new user account and returns a JWT token.
     *
     * @param request the registration request containing user details
     * @return an {@link AuthResponse} with the JWT token and user profile
     * @throws com.vms.exception.BadRequestException if the email is already registered
     */
    AuthResponse register(RegisterRequest request);
}
