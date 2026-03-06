package com.vms.dto.response;

import com.vms.enums.IdProofType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
