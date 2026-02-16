package com.webapp.demo_app.notification;

import com.webapp.demo_app.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class JobAssignedNotificationListener {

    private final EmailNotificationService emailNotificationService;

    public JobAssignedNotificationListener(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @Async("mailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobAssigned(JobAssignedNotificationEvent event) {
        if (event.employeeEmail() == null || event.employeeEmail().isBlank()) {
            log.info("Job assignment email skipped because employee has no email: {}",
                    event.employeeName());
            return;
        }

        try {
            emailNotificationService.sendJobAssignedEmail(event);
        } catch (Exception exception) {
            log.error("Failed to send job assignment email to {}",
                    event.employeeEmail(),
                    exception);
        }
    }

    @Async("mailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobUpdated(JobUpdatedNotificationEvent event) {
        if (event.employeeEmail() == null || event.employeeEmail().isBlank()) {
            log.info("Job update email skipped because employee has no email: {}",
                    event.employeeName());
            return;
        }

        try {
            emailNotificationService.sendJobUpdatedEmail(event);
        } catch (Exception exception) {
            log.error("Failed to send job update email to {}",
                    event.employeeEmail(),
                    exception);
        }
    }

    @Async("mailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobDeleted(JobDeletedNotificationEvent event) {
        if (event.employeeEmail() == null || event.employeeEmail().isBlank()) {
            log.info("Job delete email skipped because employee has no email: {}",
                    event.employeeName());
            return;
        }

        try {
            emailNotificationService.sendJobDeletedEmail(event);
        } catch (Exception exception) {
            log.error("Failed to send job delete email to {}",
                    event.employeeEmail(),
                    exception);
        }
    }
}

