package com.vms.dto.request;

import com.vms.enums.VisitStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveRejectRequest {

    @NotNull(message = "Status is required")
    private VisitStatus status;

    private String remarks;
}
