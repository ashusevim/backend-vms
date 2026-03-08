### Request Flow Example: "Associate Creates Visit Request"
```
1. Associate fills form → clicks Submit
2. Angular visitForm.value → FormData (with photo)
3. VisitRequestService.createRequest(formData) → HTTP POST /api/visit-requests
4. Request includes header: "Authorization: Bearer eyJhbGciOi..."
5. JwtAuthFilter intercepts → validates token → sets SecurityContext
6. VisitRequestController.create() receives the request
7. @PreAuthorize("hasRole('ASSOCIATE')") → checks role → ✓ allowed
8. VisitRequestService:
   a. Finds or creates Visitor entity
   b. Creates VisitRequest with status = PENDING
   c. Saves photo as byte[]
   d. Saves to database
9. Returns ApiResponse<VisitRequestResponse> → { success: true, data: {...} }
10. Angular receives response → shows toast "Request submitted!" → refreshes list
```
