package com.webapp.demo_app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notifications.email")
public record EmailNotificationProperties(
        Boolean enabled,
        String fromAddress) {
}
