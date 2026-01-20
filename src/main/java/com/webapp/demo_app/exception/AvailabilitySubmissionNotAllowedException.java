package com.webapp.demo_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AvailabilitySubmissionNotAllowedException extends RuntimeException {
    public AvailabilitySubmissionNotAllowedException(String message) {
        super(message);
    }
}
