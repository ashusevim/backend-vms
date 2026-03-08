package com.vms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource cannot be found in the system.
 *
 * <p>Automatically mapped to HTTP {@code 404 Not Found} via {@link ResponseStatus}.</p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ResourceNotFoundException} with a formatted message
     * indicating the resource type, lookup field, and value.
     *
     * @param resource the type of resource (e.g., "User", "VisitRequest")
     * @param field    the field used for lookup (e.g., "id", "email")
     * @param value    the value that was not found
     */
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: '%s'", resource, field, value));
    }
}
