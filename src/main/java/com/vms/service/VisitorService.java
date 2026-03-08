package com.vms.service;

import com.vms.dto.response.VisitorResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for visitor profile management.
 *
 * <p>Provides CRUD operations for visitor records, keyword-based search,
 * and photo upload/retrieval capabilities.</p>
 */
public interface VisitorService {

    /**
     * Retrieves a visitor by their unique ID.
     *
     * @param id the visitor's ID
     * @return the visitor's profile
     * @throws com.vms.exception.ResourceNotFoundException if the visitor does not exist
     */
    VisitorResponse getVisitorById(Long id);

    /**
     * Retrieves all visitors in the system.
     *
     * @return a list of all visitor profiles
     */
    List<VisitorResponse> getAllVisitors();

    /**
     * Searches for visitors by keyword across name, mobile number, and email.
     *
     * @param keyword the search keyword
     * @return a list of matching visitor profiles
     */
    List<VisitorResponse> searchVisitors(String keyword);

    /**
     * Creates a new visitor record.
     *
     * @param name          the visitor's full name
     * @param mobileNumber  the visitor's mobile number
     * @param email         the visitor's email address (may be {@code null})
     * @param companyName   the visitor's company name (may be {@code null})
     * @param idProofType   the type of identity proof (may be {@code null})
     * @param idProofNumber the identity proof number (may be {@code null})
     * @return the created visitor's profile
     */
    VisitorResponse createVisitor(String name, String mobileNumber, String email,
            String companyName,
            com.vms.enums.IdProofType idProofType,
            String idProofNumber);

    /**
     * Updates an existing visitor's details. Only non-null fields are updated.
     *
     * @param id           the visitor's ID
     * @param name         the new name (or {@code null} to keep unchanged)
     * @param mobileNumber the new mobile number (or {@code null} to keep unchanged)
     * @param email        the new email (or {@code null} to keep unchanged)
     * @param companyName  the new company name (or {@code null} to keep unchanged)
     * @return the updated visitor's profile
     * @throws com.vms.exception.ResourceNotFoundException if the visitor does not exist
     */
    VisitorResponse updateVisitor(Long id, String name, String mobileNumber, String email,
            String companyName);

    /**
     * Uploads a photo for a visitor.
     *
     * @param id   the visitor's ID
     * @param file the photo file (must be an image)
     * @return the updated visitor's profile with the photo path set
     * @throws com.vms.exception.BadRequestException       if the file is empty or not an image
     * @throws com.vms.exception.ResourceNotFoundException if the visitor does not exist
     */
    VisitorResponse uploadPhoto(Long id, MultipartFile file);

    /**
     * Retrieves a visitor's photo as a byte array.
     *
     * @param id the visitor's ID
     * @return the photo file contents as bytes
     * @throws com.vms.exception.ResourceNotFoundException if the visitor or photo does not exist
     */
    byte[] getPhoto(Long id);
}
