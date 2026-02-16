package com.webapp.demo_app.service;

import com.webapp.demo_app.config.EmailNotificationProperties;
import com.webapp.demo_app.notification.JobAssignedNotificationEvent;
import com.webapp.demo_app.notification.JobDeletedNotificationEvent;
import com.webapp.demo_app.notification.JobUpdatedNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailNotificationService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final EmailNotificationProperties emailProperties;

    public EmailNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider,
                                    EmailNotificationProperties emailProperties) {
        this.mailSenderProvider = mailSenderProvider;
        this.emailProperties = emailProperties;
    }

    public boolean isEnabled() {
        return emailProperties.enabled();
    }

    public void sendJobAssignedEmail(JobAssignedNotificationEvent event) {
        if (!isEnabled()) {
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Email notification is enabled, but no JavaMailSender bean is configured. Skipping email for {}",
                    event.employeeEmail());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailProperties.fromAddress());
        message.setTo(event.employeeEmail());
        message.setSubject("New Job Assignment Notification");
        message.setText(buildBody(event));

        mailSender.send(message);
        log.info("Job assignment email sent to employee={} email={}",
                event.employeeName(),
                event.employeeEmail());
    }

    public void sendJobUpdatedEmail(JobUpdatedNotificationEvent event) {
        if (!isEnabled()) {
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Email notification is enabled, but no JavaMailSender bean is configured. Skipping email for {}",
                    event.employeeEmail());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailProperties.fromAddress());
        message.setTo(event.employeeEmail());
        message.setSubject("Job Update Notification");
        message.setText(buildUpdatedBody(event));

        mailSender.send(message);
        log.info("Job update email sent to employee={} email={}",
                event.employeeName(),
                event.employeeEmail());
    }

    public void sendJobDeletedEmail(JobDeletedNotificationEvent event) {
        if (!isEnabled()) {
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Email notification is enabled, but no JavaMailSender bean is configured. Skipping email for {}",
                    event.employeeEmail());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailProperties.fromAddress());
        message.setTo(event.employeeEmail());
        message.setSubject("Job Deletion Notification");
        message.setText(buildDeletedBody(event));

        mailSender.send(message);
        log.info("Job delete email sent to employee={} email={}",
                event.employeeName(),
                event.employeeEmail());
    }

    private String buildBody(JobAssignedNotificationEvent event) {
        return "Hello " + event.employeeName() + ",\n\n"
                + "A new job has been assigned to you.\n\n"
                + "Customer: " + event.customerName() + "\n"
                + "Address: " + event.jobAddress() + "\n"
                + "Date: " + event.date() + "\n"
                + "Start Time: " + event.startTime() + "\n"
                + "Estimated Duration: " + event.estimatedDurationHours() + " hours\n\n"
                + "Please check your dashboard for details.\n\n"
                + "Thanks.";
    }

    private String buildUpdatedBody(JobUpdatedNotificationEvent event) {
        return "Hello " + event.employeeName() + ",\n\n"
                + "One of your assigned jobs has been updated.\n\n"
                + "Customer: " + event.customerName() + "\n"
                + "Address: " + event.jobAddress() + "\n"
                + "Date: " + event.date() + "\n"
                + "Start Time: " + event.startTime() + "\n"
                + "Estimated Duration: " + event.estimatedDurationHours() + " hours\n\n"
                + "Please check your dashboard for the latest details.\n\n"
                + "Thanks.";
    }

    private String buildDeletedBody(JobDeletedNotificationEvent event) {
        return "Hello " + event.employeeName() + ",\n\n"
                + "One of your assigned jobs has been deleted.\n\n"
                + "Customer: " + event.customerName() + "\n"
                + "Address: " + event.jobAddress() + "\n"
                + "Date: " + event.date() + "\n"
                + "Start Time: " + event.startTime() + "\n"
                + "Please check your dashboard for the latest details.\n\n"
                + "Thanks.";
    }
}

