package com.vms.dto.request;

import com.vms.enums.VisitStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for approving or rejecting a visit request.
 *
 * <p>The {@code status} must be either {@link VisitStatus#APPROVED} or
 * {@link VisitStatus#REJECTED}. An optional {@code remarks} field allows
 * the approver to provide a reason or note.</p>
 *
 * @see com.vms.service.VisitRequestService#approveOrReject(Long, ApproveRejectRequest, Long)
 */
@Data
public class ApproveRejectRequest {

    @NotNull(message = "Status is required")
    private VisitStatus status;

    private String remarks;
}
