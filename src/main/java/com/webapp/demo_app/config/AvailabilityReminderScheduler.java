package com.webapp.demo_app.config;
import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.service.EmailNotificationService;
import com.webapp.demo_app.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvailabilityReminderScheduler {

    private final EmployeeService employeeService;
    private final EmailNotificationService emailNotificationService;

    @Scheduled(cron = "0 0 10 ? * SUN")
    public void sendMorningsundayAvailabiltyReminder(){
        sendSundayAvailabilityReminderEmails("10:00");
    }

    @Scheduled(cron = "0 30 23 ? * SUN")
    public void sendLateSundayAvailabilityReminder() {
        sendSundayAvailabilityReminderEmails("23:30");
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

        log.info("Sunday availability reminder run completed at {}. Sent emails count={}",
                triggerTime,
                sentCount);
    }
}
