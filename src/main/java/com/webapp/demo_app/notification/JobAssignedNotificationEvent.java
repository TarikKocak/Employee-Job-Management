package com.webapp.demo_app.notification;

import java.time.LocalDate;
import java.time.LocalTime;

public record JobAssignedNotificationEvent(
        String employeeName,
        String employeeEmail,
        String customerName,
        String jobAddress,
        LocalDate date,
        LocalTime startTime,
        Double estimatedDurationHours
) {
}
