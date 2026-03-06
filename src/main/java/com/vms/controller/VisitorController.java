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

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VisitorResponse>>> getAllVisitors() {
        return ResponseEntity.ok(ApiResponse.success(visitorService.getAllVisitors()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitorResponse>> getVisitorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(visitorService.getVisitorById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VisitorResponse>>> searchVisitors(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(visitorService.searchVisitors(keyword)));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VisitorResponse>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully",
                visitorService.uploadPhoto(id, file)));
    }

    @GetMapping(value = "/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        byte[] photo = visitorService.getPhoto(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
    }
}
