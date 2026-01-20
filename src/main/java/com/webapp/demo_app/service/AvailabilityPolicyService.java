package com.webapp.demo_app.service;

import com.webapp.demo_app.exception.AvailabilitySubmissionNotAllowedException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class AvailabilityPolicyService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");

    public boolean isSunday() {
        return LocalDate.now(ZONE).getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public void assertSubmissionAllowed() {
        if (!isSunday()) {
            throw new AvailabilitySubmissionNotAllowedException(
                    "Bu tabloyu sadece Pazar günleri doldurabilir veya değişiklik yapabilirsiniz."
            );
        }
    }
}
