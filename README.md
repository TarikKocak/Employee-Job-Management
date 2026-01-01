# Employee Job & Availability Management System

This project is a **Spring Boot–based web application** designed to manage employees, their job assignments, and weekly availability schedules.  
It supports **role-based usage** (Admin & Employee) and provides an advanced **availability planning and validation system**.

---

##  Features

### 👤 Employee Side
- Employee dashboard
- View current and completed jobs
- Edit job-related fields (duration, tips, etc.)
- Weekly availability planning
- Availability rules enforcement:
  - Minimum **4 different days**
  - Each day must have **at least 5 consecutive available hours**
- Two-week availability view
- Submit availability **in bulk** (not per click)

---

### 🛠 Admin Side
- Employee list with filtering (by Titles and name)
- Assign jobs to employees
- Block employee availability automatically when a job is assigned
- **Admin Availability Overview Table**
  - Displays the overlapping availability of all employees
  - Hover over available slots to see **who is available**
  - Helps with scheduling and workforce planning

---

##  Availability Logic

- Availability is displayed as a **7 × 10 grid** (7 days × 10 working hours)
- Slot status:
  - `0` → Not available (grey)
  - `1` → Available (green)
  - `2` → Occupied / Blocked by job (red)
- Employees select slots visually and submit them together
- Validation is applied **per week**

---

##  Project Structure
# Employee Job & Availability Management System

This project is a **Spring Boot–based web application** designed to manage employees, their job assignments, and weekly availability schedules.  
It supports **role-based usage** (Admin & Employee) and provides an advanced **availability planning and validation system**.

---


## 🗄 Database

- **PostgreSQL**
- Main tables:
  - `admin` 
  - `employees`
  - `availability_slots`
  - `mevcut_isler` (current jobs)
  - `tamamlanan_isler` (completed jobs)

---

##  Technologies Used

- Java 21+
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- HTML / CSS / JavaScript
- Maven

---


## 🔒 Security Notes

- Password encoding
- Login-based access control
- URL manipulation prevention
- Role-based access (ADMIN / EMPLOYEE)


---

## 📌 Future Improvements

- Conflict detection for job assignments
- AJAX-based filtering and tooltips
- 
---

## IMPORTANT NOTES:
-This project demonstrates advanced and complex Spring Boot web application concepts. While you are welcome to explore, study, or reuse parts of the code, please be aware that this project is not production-ready and does not follow all best practices. Use it at your own risk. Before applying any part of this project in a real-world or commercial environment, consult a qualified professional to review architecture, security, and performance considerations.

-Since there is no admin entity (without an admin, it is impossible to create an employee object), I implemented an initial admin entity creation inside src/main/java/com/webapp/demo_app/config/AdminBootstrap. After the initial run, an admin entity will be created to test the login form.

---



