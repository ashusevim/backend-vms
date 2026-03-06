package com.vms.service;

import com.vms.dto.response.VisitorResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VisitorService {
    VisitorResponse getVisitorById(Long id);

    List<VisitorResponse> getAllVisitors();

    List<VisitorResponse> searchVisitors(String keyword);

    VisitorResponse createVisitor(String name, String mobileNumber, String email,
            String companyName,
            com.vms.enums.IdProofType idProofType,
            String idProofNumber);

    VisitorResponse updateVisitor(Long id, String name, String mobileNumber, String email,
            String companyName);

    VisitorResponse uploadPhoto(Long id, MultipartFile file);

    byte[] getPhoto(Long id);
}
