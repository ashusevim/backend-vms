package com.vms.controller;

import com.vms.dto.response.ApiResponse;
import com.vms.dto.response.VisitorResponse;
import com.vms.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for visitor management endpoints.
 *
 * <p>Provides endpoints to list, search, and retrieve visitors, as well as
 * upload and retrieve visitor photos. All endpoints require authentication.</p>
 */
@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    /**
     * Retrieves all visitors.
     *
     * @return a {@code 200 OK} response with the list of all visitors
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VisitorResponse>>> getAllVisitors() {
        return ResponseEntity.ok(ApiResponse.success(visitorService.getAllVisitors()));
    }

    /**
     * Retrieves a visitor by their ID.
     *
     * @param id the visitor's ID
     * @return a {@code 200 OK} response with the visitor profile
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitorResponse>> getVisitorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(visitorService.getVisitorById(id)));
    }

    /**
     * Searches for visitors by keyword across name, mobile number, and email.
     *
     * @param keyword the search keyword
     * @return a {@code 200 OK} response with matching visitors
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VisitorResponse>>> searchVisitors(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(visitorService.searchVisitors(keyword)));
    }

    /**
     * Uploads a photo for a visitor.
     *
     * @param id   the visitor's ID
     * @param file the photo file (must be an image)
     * @return a {@code 200 OK} response with the updated visitor profile
     */
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VisitorResponse>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully",
                visitorService.uploadPhoto(id, file)));
    }

    /**
     * Retrieves a visitor's photo as a JPEG image.
     *
     * @param id the visitor's ID
     * @return a {@code 200 OK} response with the photo bytes
     */
    @GetMapping(value = "/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        byte[] photo = visitorService.getPhoto(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
    }
}
