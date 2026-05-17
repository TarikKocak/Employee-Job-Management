# Employee Job & Availability Management System

A Spring Boot web application for managing employee operations end-to-end:
- authentication and role-based access (`ADMIN`, `EMPLOYEE`)
- job assignment and lifecycle tracking (current/completed)
- weekly availability planning with validation policies
- admin dashboards and system settings
- email notifications and scheduled background tasks
---

## Features

### Employee features
- Login and personal dashboard
- View/update current jobs
- View completed jobs
- Submit weekly availability in a grid-based UI
- Availability policy enforcement (minimum day/hour rules)

---

### Admin features
- Employee listing and filtering
- Add/edit employee records
- Assign jobs
- View job overview and completed job records
- Inspect employee availability data
- Configure system settings

---

## Availability model

- Weekly grid: **7 days × 10 working-hour slots**
- Slot values:
  - `0` = unavailable
  - `1` = available
  - `2` = occupied (e.g., blocked by assigned work)
- Validation is applied per submitted week.

---

##  Tech stack

- Java 21
- Spring Boot (MVC, Data JPA, Security)
- Thymeleaf
- PostgreSQL
- HTML/CSS/JavaScript
- Maven

---
## Current project structure

```text
Employee-Job-Management/
├─ pom.xml
├─ mvnw, mvnw.cmd
├─ README.md
└─ src/
   ├─ main/
   │  ├─ java/com/webapp/demo_app/
   │  │  ├─ DemoAppApplication.java
   │  │  ├─ config/
   │  │  │  ├─ AdminBootstrap.java
   │  │  │  ├─ AsyncConfig.java
   │  │  │  ├─ AvailabilityCleanupScheduler.java
   │  │  │  ├─ AvailabilityReminderScheduler.java
   │  │  │  ├─ EmailNotificationProperties.java
   │  │  │  ├─ GlobalExceptionHandler.java
   │  │  │  ├─ LoggingContextFilter.java
   │  │  │  ├─ SecurityConfig.java
   │  │  │  └─ SystemSettingsInitializer.java
   │  │  ├─ controller/
   │  │  │  ├─ AdminController.java
   │  │  │  ├─ AuthController.java
   │  │  │  ├─ EmployeeController.java
   │  │  │  └─ SettingsController.java
   │  │  ├─ dto/
   │  │  ├─ exception/
   │  │  ├─ model/
   │  │  │  └─ enums/
   │  │  ├─ notification/
   │  │  ├─ repository/
   │  │  ├─ security/
   │  │  └─ service/
   │  └─ resources/
   │     ├─ application.properties
   │     ├─ application-local.properties
   │     ├─ application-prod.properties
   │     ├─ logback-spring.xml
   │     ├─ static/
   │     │  ├─ css/
   │     │  ├─ js/
   │     │  └─ EM.png
   │     └─ templates/
   │        ├─ admin/
   │        ├─ auth/
   │        ├─ employee/
   │        ├─ error/
   │        ├─ fragments/
   │        └─ settings/
   └─ test/
      └─ java/com/webapp/demo_app/DemoAppApplicationTests.java
```

---

## Database

Primary persisted domain objects include:
- `Admin`
- `Employee`
- `AvailabilitySlot`
- `MevcutIs` (current jobs)
- `TamamlananIs` (completed jobs)
- `SystemSettings`

---

## Important notes

- This repository is educational/demo-oriented and not production-hardened.
- Initial admin provisioning is bootstrapped via `config/AdminBootstrap.java` so the first login flow can be tested.



