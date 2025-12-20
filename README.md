# Employee Job & Availability Management System

This project is a **Spring Boot–based web application** designed to manage employees, their job assignments, and weekly availability schedules.  
It supports **role-based usage** (Admin & Employee) and provides an advanced **availability planning and validation system**.

---

## 🚀 Features

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
- Employee list with filtering (by ID and name)
- Assign jobs to employees
- Block employee availability automatically when a job is assigned
- **Admin Availability Overview Table**
  - Displays overlapping availability of all employees
  - Hover over available slots to see **who is available**
  - Helps with scheduling and workforce planning

---

## 🧠 Availability Logic

- Availability is displayed as a **7 × 10 grid** (7 days × 10 working hours)
- Slot status:
  - `0` → Not available (gray)
  - `1` → Available (green)
  - `2` → Occupied / Blocked by job (red)
- Employees select slots visually and submit them together
- Validation is applied **per week**

---

## 🏗 Project Structure
# Employee Job & Availability Management System

This project is a **Spring Boot–based web application** designed to manage employees, their job assignments, and weekly availability schedules.  
It supports **role-based usage** (Admin & Employee) and provides an advanced **availability planning and validation system**.

---

## 🚀 Features

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
- Employee list with filtering (by ID and name)
- Assign jobs to employees
- Block employee availability automatically when a job is assigned
- **Admin Availability Overview Table**
  - Displays overlapping availability of all employees
  - Hover over available slots to see **who is available**
  - Helps with scheduling and workforce planning

---

## 🧠 Availability Logic

- Availability is displayed as a **7 × 10 grid** (7 days × 10 working hours)
- Slot status:
  - `0` → Not available (gray)
  - `1` → Available (green)
  - `2` → Occupied / Blocked by job (red)
- Employees select slots visually and submit them together
- Validation is applied **per week**

---



## 🗄 Database

- **PostgreSQL**
- Main tables:
  - `employees`
  - `availability_slots`
  - `mevcut_isler` (current jobs)
  - `tamamlanan_isler` (completed jobs)

---

## 🧩 Technologies Used

- Java 17+
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- PostgreSQL
- HTML / CSS / JavaScript
- Maven

---

## ✅ Key Design Decisions

- Availability is **not saved per click**
- Validation is done **before persistence**
- Admin availability table is built using **aggregated availability data**
- Clean separation of concerns (Controller → Service → Repository)

---

## 🔒 Security Notes (Planned / Optional)

- Password encoding
- Login-based access control
- URL manipulation prevention
- Role-based access (ADMIN / EMPLOYEE)

---

## 📌 Future Improvements

- Weekly navigation for admin availability
- Heatmap view for availability density
- Conflict detection for job assignments
- AJAX-based filtering and tooltips
- Spring Security integration

---

## 👨‍💻 Author

**Tarık Koçak**  
Software Engineer 
Backend & Full-Stack Development Enthusiast

---

## 📄 License

This project is for educational and personal use.

