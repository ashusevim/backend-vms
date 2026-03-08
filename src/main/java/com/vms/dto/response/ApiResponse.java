package com.vms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper used across all REST endpoints.
 *
 * <p>Provides a consistent response structure containing a success flag,
 * a human-readable message, and an optional data payload of type {@code T}.
 * Null fields are excluded from the JSON output.</p>
 *
 * @param <T> the type of the response payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Indicates whether the request was processed successfully. */
    private boolean success;

    /** A descriptive message about the result of the operation. */
    private String message;

    /** The response payload; {@code null} for error responses or void operations. */
    private T data;

    /**
     * Creates a successful response with the given data and a default "Success" message.
     *
     * @param data the response payload
     * @param <T>  the type of the payload
     * @return a successful {@link ApiResponse}
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .build();
    }

    /**
     * Creates a successful response with a custom message and data payload.
     *
     * @param message a descriptive success message
     * @param data    the response payload
     * @param <T>     the type of the payload
     * @return a successful {@link ApiResponse}
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates an error response with the given message and no data payload.
     *
     * @param message a descriptive error message
     * @param <T>     the type of the (absent) payload
     * @return an error {@link ApiResponse}
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
