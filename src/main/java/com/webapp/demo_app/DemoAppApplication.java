package com.webapp.demo_app;

import com.webapp.demo_app.config.EmailNotificationProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZonedDateTime;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(EmailNotificationProperties.class)
public class DemoAppApplication {

	public static void main(String[] args) {
		System.out.println("Spring Boot is starting...");
		System.out.println("Running Java version: " + System.getProperty("java.version"));
		ZonedDateTime now = ZonedDateTime.now();
		System.out.println("Timezone: " + now.getZone());
		System.out.println("Current Time: " + now);
		SpringApplication.run(DemoAppApplication.class, args);
	}

}
