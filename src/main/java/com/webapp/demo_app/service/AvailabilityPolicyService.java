package com.webapp.demo_app.service;

import com.webapp.demo_app.exception.AvailabilitySubmissionNotAllowedException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;


@Service
public class AvailabilityPolicyService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");

    private final SystemSettingsService systemSettingsService;

    public AvailabilityPolicyService(SystemSettingsService systemSettingsService) {
        this.systemSettingsService = systemSettingsService;
    }

    public boolean isSunday() {
        return LocalDate.now(ZONE).getDayOfWeek() == DayOfWeek.SUNDAY;
    }
    public boolean canSubmitAvailability() {

        // If the admin turn off globally → Availability can be submitted everytime
        if (!systemSettingsService.isAvailabilitySundayOnlyEnabled()) {
            return true;
        }

        // If turned on  → can only be submitted on Sundays
        return isSunday();
    }

    public void assertSubmissionAllowed() {


        if (!systemSettingsService.isAvailabilitySundayOnlyEnabled()) {
            return;
        }


        if (!isSunday()) {
            throw new AvailabilitySubmissionNotAllowedException(
                    "Bu tabloyu sadece Pazar günleri doldurabilir veya değişiklik yapabilirsiniz."
            );
        }
    }
}
