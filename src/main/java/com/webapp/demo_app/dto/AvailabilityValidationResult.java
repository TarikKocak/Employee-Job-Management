package com.webapp.demo_app.dto;


// For minimum availability validation

public record AvailabilityValidationResult(
        boolean valid,
        String message
) {}