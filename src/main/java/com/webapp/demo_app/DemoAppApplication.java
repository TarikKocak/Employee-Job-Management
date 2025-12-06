package com.webapp.demo_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoAppApplication {

	public static void main(String[] args) {
		System.out.println("Spring Boot is starting...");
		System.out.println("Running Java version: " + System.getProperty("java.version"));
		SpringApplication.run(DemoAppApplication.class, args);
	}

}
