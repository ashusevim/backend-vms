package com.vms.service.impl;

import com.vms.dto.response.VisitorResponse;
import com.vms.entity.Visitor;
import com.vms.enums.IdProofType;
import com.vms.exception.BadRequestException;
import com.vms.exception.ResourceNotFoundException;
import com.vms.repository.VisitorRepository;
import com.vms.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link VisitorService} providing visitor CRUD and photo management.
 *
 * <p>Supports creating and updating visitor profiles, keyword-based search,
 * and uploading/retrieving visitor photos stored on the local filesystem.</p>
 *
 * @see VisitorService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;

    @Value("${app.upload.dir:uploads/visitor-photos}")
    private String uploadDir;

    @Override
    public VisitorResponse getVisitorById(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "id", id));
        return mapToResponse(visitor);
    }

    @Override
    public List<VisitorResponse> getAllVisitors() {
        return visitorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitorResponse> searchVisitors(String keyword) {
        return visitorRepository.searchByKeyword(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VisitorResponse createVisitor(String name, String mobileNumber, String email,
            String companyName, IdProofType idProofType,
            String idProofNumber) {
        Visitor visitor = Visitor.builder()
                .name(name)
                .mobileNumber(mobileNumber)
                .email(email)
                .companyName(companyName)
                .idProofType(idProofType)
                .idProofNumber(idProofNumber)
                .build();

        visitor = visitorRepository.save(visitor);
        return mapToResponse(visitor);
    }

    @Override
    public VisitorResponse updateVisitor(Long id, String name, String mobileNumber,
            String email, String companyName) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "id", id));

        if (name != null)
            visitor.setName(name);
        if (mobileNumber != null)
            visitor.setMobileNumber(mobileNumber);
        if (email != null)
            visitor.setEmail(email);
        if (companyName != null)
            visitor.setCompanyName(companyName);

        visitor = visitorRepository.save(visitor);
        return mapToResponse(visitor);
    }

    @Override
    public VisitorResponse uploadPhoto(Long id, MultipartFile file) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "id", id));

        if (file.isEmpty()) {
            throw new BadRequestException("Photo file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension = getFileExtension(file.getOriginalFilename());
            String filename = "visitor_" + id + "_" + UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(filename);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            visitor.setPhotoPath(filePath.toString());
            visitor = visitorRepository.save(visitor);

            log.info("Photo uploaded for visitor {}: {}", id, filePath);
            return mapToResponse(visitor);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload photo: " + e.getMessage());
        }
    }

    @Override
    public byte[] getPhoto(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "id", id));

        if (visitor.getPhotoPath() == null) {
            throw new ResourceNotFoundException("Photo", "visitorId", id);
        }

        try {
            Path photoPath = Paths.get(visitor.getPhotoPath());
            return Files.readAllBytes(photoPath);
        } catch (IOException e) {
            throw new BadRequestException("Failed to read photo: " + e.getMessage());
        }
    }

    /**
     * Extracts the file extension from a filename.
     *
     * @param filename the original filename
     * @return the file extension including the dot (e.g., ".jpg"), defaults to ".jpg"
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf('.'));
    }

    /**
     * Maps a {@link Visitor} entity to a {@link VisitorResponse} DTO.
     *
     * @param visitor the visitor entity
     * @return the mapped response DTO
     */
    private VisitorResponse mapToResponse(Visitor visitor) {
        return VisitorResponse.builder()
                .id(visitor.getId())
                .name(visitor.getName())
                .mobileNumber(visitor.getMobileNumber())
                .email(visitor.getEmail())
                .companyName(visitor.getCompanyName())
                .idProofType(visitor.getIdProofType())
                .idProofNumber(visitor.getIdProofNumber())
                .photoPath(visitor.getPhotoPath())
                .createdAt(visitor.getCreatedAt())
                .build();
    }
}
