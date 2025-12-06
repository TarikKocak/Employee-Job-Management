package com.webapp.demo_app.repository;

import com.webapp.demo_app.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByName(String name); // ileride login için
}