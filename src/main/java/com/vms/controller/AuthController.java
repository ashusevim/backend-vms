package com.vms.controller;

import com.vms.dto.request.LoginRequest;
import com.vms.dto.request.RegisterRequest;
import com.vms.dto.response.ApiResponse;
import com.vms.dto.response.AuthResponse;
import com.vms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller handling authentication endpoints.
 *
 * <p>Provides public (unauthenticated) endpoints for user login and registration.
 * All endpoints are under {@code /api/auth}.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token with user profile details.
     *
     * @param request the login credentials
     * @return a {@code 200 OK} response with the JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * Registers a new user account and returns a JWT token.
     *
     * @param request the registration details
     * @return a {@code 201 Created} response with the JWT token and user info
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }
}
