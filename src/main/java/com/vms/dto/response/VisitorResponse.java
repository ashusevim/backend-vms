package com.vms.dto.response;

import com.vms.enums.IdProofType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO representing a visitor's profile information.
 *
 * <p>Includes personal details, identification information, and the path
 * to the visitor's uploaded photo (if available).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorResponse {

    private Long id;
    private String name;
    private String mobileNumber;
    private String email;
    private String companyName;
    private IdProofType idProofType;
    private String idProofNumber;
    private String photoPath;
    private LocalDateTime createdAt;
}
