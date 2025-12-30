package com.webapp.demo_app.service;


import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.model.enums.EmployeeeTitle;
import com.webapp.demo_app.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // READ OPERATIONS
    // =========================

    public Employee getById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Employee not found: " + employeeId));
    }

    public List<Employee> getByTitle(EmployeeeTitle title) {
        return employeeRepository.findByTitle(title);
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }


    // =========================
    // WRITE OPERATIONS
    // =========================

    @Transactional
    public Employee save(Employee employee) {
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    @Transactional
    public void delete(Long employeeId) {
        Employee employee = getById(employeeId);
        employeeRepository.delete(employee);
    }
}
