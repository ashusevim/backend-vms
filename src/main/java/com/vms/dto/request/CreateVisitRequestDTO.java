package com.vms.dto.request;

import com.vms.enums.IdProofType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateVisitRequestDTO {

    @NotBlank(message = "Purpose is required")
    private String purpose;

    private String category;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    private LocalTime inTime;

    private LocalTime outTime;

    @NotNull(message = "Associate ID is required")
    private Long associateId;

    // Visitor details (can provide existing visitorId OR new visitor info)
    private Long visitorId;

    // New visitor fields (used when visitorId is null)
    private String visitorName;
    private String visitorMobile;
    private String visitorEmail;
    private String visitorCompany;
    private IdProofType visitorIdProofType;
    private String visitorIdProofNumber;

    // For group visits: list of additional visitors
    private List<VisitorInfo> additionalVisitors;

    @Data
    public static class VisitorInfo {
        private String name;
        private String mobileNumber;
        private String email;
        private String companyName;
        private IdProofType idProofType;
        private String idProofNumber;
    }
}
