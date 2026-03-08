package com.vms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO representing a visit log entry (check-in/check-out record).
 *
 * <p>Includes denormalized visitor, associate, and security guard names
 * to avoid additional lookups on the client side.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitLogResponse {

    private Long id;
    private Long visitRequestId;
    private String visitorName;
    private String associateName;
    private String purpose;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String securityGuardName;
    private String badgeNumber;
}
