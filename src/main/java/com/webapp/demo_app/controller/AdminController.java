package com.webapp.demo_app.controller;


import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EmployeeRepository employeeRepository;

    public AdminController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Admin dashboard
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/admin-dashboard";
    }

    // Employee List
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "admin/admin-employees-list";
    }

    // To access the employee creation form
    @GetMapping("/add-employee")
    public String addEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "admin/admin-add-employee";
    }

    @PostMapping("/add-employee")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeRepository.save(employee);
        return "redirect:/admin/employees";
    }


    // Viewing emplyee details
    @GetMapping("/employees/{id}")
    public String showEmployee(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id).orElse(null);

        model.addAttribute("employee", employee);
        model.addAttribute("currentJobs", employee.getMevcutIsler());
        model.addAttribute("completedJobs", employee.getTamamlananIsler());

        return "admin/admin-employees-details";
    }

}
