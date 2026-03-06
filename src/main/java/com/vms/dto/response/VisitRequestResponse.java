package com.vms.dto.response;

import com.vms.enums.VisitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitRequestResponse {

    private Long id;
    private String purpose;
    private String category;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalTime inTime;
    private LocalTime outTime;
    private VisitStatus status;
    private String remarks;
    private String qrCodeToken;
    private String groupId;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    // Visitor info
    private Long visitorId;
    private String visitorName;
    private String visitorMobile;
    private String visitorEmail;
    private String visitorCompany;

    // Associate info
    private Long associateId;
    private String associateName;
    private String associateDepartment;

    // Approved by
    private String approvedByName;
}
