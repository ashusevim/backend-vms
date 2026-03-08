package com.vms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for visitor check-in requests.
 *
 * <p>A visitor can be checked in either by providing the {@code visitRequestId}
 * or by scanning the {@code qrCodeToken} generated upon approval. At least one
 * of these fields must be supplied.</p>
 *
 * <p>An optional {@code badgeNumber} can be assigned to the visitor during check-in.</p>
 *
 * @see com.vms.service.VisitLogService#checkIn(CheckInRequest, Long)
 */
@Data
public class CheckInRequest {

    @NotNull(message = "Visit request ID is required")
    private Long visitRequestId;

    private String badgeNumber;

    // Can also check in using QR code token
    private String qrCodeToken;
}
