package com.vms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client request contains invalid or unprocessable data.
 *
 * <p>Automatically mapped to HTTP {@code 400 Bad Request} via {@link ResponseStatus}.</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new {@code BadRequestException} with the specified detail message.
     *
     * @param message the detail message explaining the bad request
     */
    public BadRequestException(String message) {
        super(message);
    }
}
