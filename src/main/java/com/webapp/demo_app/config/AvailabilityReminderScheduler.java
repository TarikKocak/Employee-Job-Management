package com.webapp.demo_app.config;
import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.service.EmailNotificationService;
import com.webapp.demo_app.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Component
@RequiredArgsConstructor
public class AvailabilityReminderScheduler {

    private static final ZoneId BERLIN_ZONE = ZoneId.of("Europe/Berlin");
    private static final DateTimeFormatter LOG_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");

    private final EmployeeService employeeService;
    private final EmailNotificationService emailNotificationService;

    @Scheduled(cron = "0 0 10 ? * SUN", zone = "Europe/Berlin")
    public void sendMorningsundayAvailabiltyReminder(){
        sendSundayAvailabilityReminderEmails("10:00");
    }

    @Scheduled(cron = "0 30 22 ? * SUN", zone = "Europe/Berlin")
    public void sendLateSundayAvailabilityReminder() {
        sendSundayAvailabilityReminderEmails("22:30");
    }

    private void sendSundayAvailabilityReminderEmails(String triggerTime) {
        int sentCount = 0;

        for (Employee employee : employeeService.getAll()) {
            if (employee.getEmail() == null || employee.getEmail().isBlank()) {
                continue;
            }

            try {
                emailNotificationService.sendAvailabilityReminderEmail(employee.getUsername(), employee.getEmail());
                sentCount++;
            } catch (Exception exception) {
                log.error("Failed to send Sunday availability reminder to {}",
                        employee.getEmail(),
                        exception);
            }
        }

        String munichRunTime = ZonedDateTime.now(BERLIN_ZONE).format(LOG_TIME_FORMATTER);
        log.info("Sunday availability reminder run completed at {} Munich time (trigger {}). Sent emails count={}",
                munichRunTime,
                triggerTime,
                sentCount);
    }
}
