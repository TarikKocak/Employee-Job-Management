package com.webapp.demo_app.config;

import com.webapp.demo_app.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AvailabilityCleanupScheduler {

    private final AvailabilityService availabilityService;

    /**
     * Runs every Sunday at 00:00
     */
    @Scheduled(cron = "0 0 0 ? * SUN")
    public void cleanupExpiredAvailability() {
        //log.info("Weekly availability cleanup started");
        availabilityService.removeExpiredAvailabilitySlots();
        //log.info("Weekly availability cleanup finished");
    }
}