package com.vms.dto.response;

import com.vms.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO representing a user's profile information.
 *
 * <p>Contains non-sensitive user details suitable for API responses.
 * The password field is intentionally excluded.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String department;
    private String designation;
    private Role role;
}
