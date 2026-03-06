package com.vms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInRequest {

    @NotNull(message = "Visit request ID is required")
    private Long visitRequestId;

    private String badgeNumber;

    // Can also check in using QR code token
    private String qrCodeToken;
}
