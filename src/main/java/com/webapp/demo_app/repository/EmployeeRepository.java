package com.webapp.demo_app.repository;

import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.model.enums.EmployeeeTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByName(String name); // for futer login implementation
    //Employee findById(long id);
    //Optional<Employee> findById(Long id);
    List<Employee> findByTitle(EmployeeeTitle title);
}