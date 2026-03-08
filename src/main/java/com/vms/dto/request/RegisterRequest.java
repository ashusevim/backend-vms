package com.vms.dto.request;

import com.vms.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for new user registration requests.
 *
 * <p>Captures all required fields to create a new user account in the system,
 * including personal details and the assigned {@link Role}.</p>
 *
 * @see com.vms.controller.AuthController#register(RegisterRequest)
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

    private String department;

    private String designation;

    @NotNull(message = "Role is required")
    private Role role;
}
