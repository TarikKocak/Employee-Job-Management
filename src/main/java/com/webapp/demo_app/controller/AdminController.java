package com.webapp.demo_app.controller;


import com.webapp.demo_app.model.*;
import com.webapp.demo_app.repository.EmployeeRepository;
import com.webapp.demo_app.service.AvailabilityService;
import com.webapp.demo_app.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EmployeeRepository employeeRepository;
    private final JobService jobService;
    private final AvailabilityService availabilityService;

    public AdminController(EmployeeRepository employeeRepository,
                           JobService jobService,
                           AvailabilityService availabilityService) {
        this.employeeRepository = employeeRepository;
        this.jobService = jobService;
        this.availabilityService = availabilityService;
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

    @GetMapping("/employees/{id}/assign-job")
    public String showAssignJob(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Employee not found"));

        // ==========
        //  JOB FORM
        // ==========
        MevcutIs job = new MevcutIs();
        job.setEmployee(employee);

        // ==========================
        //  AVAILABILITY (READ ONLY)
        // ==========================
        LocalDate week1Monday = availabilityService.getNextWeekMonday();
        LocalDate week2Monday = week1Monday.plusWeeks(1);

        List<LocalDate> week1Dates = availabilityService.getWeekDates(week1Monday);
        List<LocalDate> week2Dates = availabilityService.getWeekDates(week2Monday);

        List<AvailabilitySlot> week1Slots =
                availabilityService.getWeekSlots(id, week1Monday);

        List<AvailabilitySlot> week2Slots =
                availabilityService.getWeekSlots(id, week2Monday);

        Map<String, Integer> week1StatusMap =
                availabilityService.buildStatusMap(week1Slots);

        Map<String, Integer> week2StatusMap =
                availabilityService.buildStatusMap(week2Slots);

        model.addAttribute("employee", employee);
        model.addAttribute("job", job);
        model.addAttribute("turler", Tur.values());
        model.addAttribute("ucretTipleri", UcretTahsilTipi.values());


        model.addAttribute("hours", availabilityService.getHours());
        model.addAttribute("week1Dates", week1Dates);
        model.addAttribute("week2Dates", week2Dates);
        model.addAttribute("week1StatusMap", week1StatusMap);
        model.addAttribute("week2StatusMap", week2StatusMap);

        return "admin/admin-assign-job";
    }

    @PostMapping("/employees/{id}/assign-job")
    public String assignJob(@PathVariable Long id, @ModelAttribute("job") MevcutIs job) {

        jobService.assignJobToEmployee(id, job);

        return "redirect:/admin/employees/" + id;
    }

}
