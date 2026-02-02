package com.webapp.demo_app.controller;

import com.webapp.demo_app.dto.AdminEmployeeUpdateDto;
import com.webapp.demo_app.model.*;
import com.webapp.demo_app.model.enums.EmployeeeTitle;
import com.webapp.demo_app.model.enums.Tur;
import com.webapp.demo_app.model.enums.UcretTahsilTipi;
import com.webapp.demo_app.repository.MevcutIsRepository;
import com.webapp.demo_app.service.AvailabilityService;
import com.webapp.demo_app.service.EmployeeService;
import com.webapp.demo_app.service.JobService;
import com.webapp.demo_app.service.SystemSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EmployeeService employeeService;
    private final JobService jobService;
    private final AvailabilityService availabilityService;
    private final MevcutIsRepository mevcutIsRepository;
    private final SystemSettingsService systemSettingsService;

    public AdminController(EmployeeService employeeService,
                           JobService jobService,
                           AvailabilityService availabilityService,
                           MevcutIsRepository mevcutIsRepository,
                           SystemSettingsService systemSettingsService) {
        this.employeeService = employeeService;
        this.jobService = jobService;
        this.availabilityService = availabilityService;
        this.mevcutIsRepository=mevcutIsRepository;
        this.systemSettingsService = systemSettingsService;
    }

    // Admin dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        boolean sundayOnlyEnabled =
                systemSettingsService.isAvailabilitySundayOnlyEnabled();

        model.addAttribute("availabilitySundayOnly", sundayOnlyEnabled);
        log.info("Admin dashboard accessed");
        return "admin/admin-dashboard";
    }

    @GetMapping("/all-employees")
    public String listAllEmployees(Model model) {

        log.info("All employees list viewed");
        List<Employee> employees = employeeService.getAll();

        model.addAttribute("employees", employees);

        return "admin/admin-employee-list";
    }

    @GetMapping("/employees")
    public String listFilteredEmployees(
            @RequestParam(required = false) EmployeeeTitle title,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Integer minHours,
            Model model
    ) {

        // normalize minHours
        if (minHours != null && minHours < 1) {
            minHours = null;
        }

        // 🔹 Dropdown için HER ZAMAN tüm employee’ler
        List<Employee> allEmployees = employeeService.getAll();

        // 🔹 Base filtering (title / employee)
        List<Employee> filteredEmployees;

        if (employeeId != null) {
            filteredEmployees =
                    List.of(employeeService.getById(employeeId));
        } else if (title != null) {
            filteredEmployees =
                    employeeService.getByTitle(title);
        } else {
            filteredEmployees = allEmployees;
        }

        //  APPLY MIN ADJACENT HOURS FILTER
        if (minHours != null) {
            filteredEmployees =
                    availabilityService.filterEmployeesByMinAdjacentHours(
                            filteredEmployees, minHours
                    );
        }

        model.addAttribute("employees", filteredEmployees);
        model.addAttribute("allEmployees", allEmployees);

        // ==================================================
        // AVAILABILITY OVERLAY (MIN HOURS AWARE)
        // ==================================================

        LocalDate week1Monday =
                availabilityService.getNextWeekMonday();
        LocalDate week2Monday =
                week1Monday.plusWeeks(1);

        Map<String, Map<String, Integer>> week1Overlap =
                availabilityService.getOverlappingAvailabilityForWeek(
                        week1Monday,
                        filteredEmployees,
                        minHours
                );

        Map<String, Map<String, Integer>> week2Overlap =
                availabilityService.getOverlappingAvailabilityForWeek(
                        week2Monday,
                        filteredEmployees,
                        minHours
                );

        model.addAttribute("hours",
                availabilityService.getHours());

        model.addAttribute("week1Dates",
                availabilityService.getWeekDates(week1Monday));

        model.addAttribute("week2Dates",
                availabilityService.getWeekDates(week2Monday));

        model.addAttribute("week1OverlapMap", week1Overlap);
        model.addAttribute("week2OverlapMap", week2Overlap);

        model.addAttribute("week1AvailableCountMap",
                availabilityService.buildAvailableCountMap(week1Overlap));

        model.addAttribute("week2AvailableCountMap",
                availabilityService.buildAvailableCountMap(week2Overlap));

        // 🔹 Keep filters selected in UI
        model.addAttribute("selectedTitle", title);
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("selectedMinHours", minHours);

        return "admin/admin-employees-list-filtered";
    }

    // To access the employee creation form
    @GetMapping("/add-employee")
    public String addEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("titles", EmployeeeTitle.values());
        return "admin/admin-add-employee";
    }

    @PostMapping("/add-employee")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeService.save(employee);
        return "redirect:/admin/employees";
    }


    // Viewing employee details
    @GetMapping("/employees/{id}")
    public String showEmployee(@PathVariable Long id,
                               @RequestParam(required = false, defaultValue = "/admin/employees") String returnUrl,
                               Model model) {
        Employee employee = employeeService.getById(id);

        model.addAttribute("employee", employee);
        model.addAttribute("currentJobs", employee.getMevcutIsler());
        model.addAttribute("completedJobs", employee.getTamamlananIsler());
        model.addAttribute("returnUrl", returnUrl);


        return "admin/admin-employees-details";
    }

    @GetMapping("/employees/{id}/assign-job")
    public String showAssignJob(@PathVariable Long id, Model model,
                                @RequestParam(required = false, defaultValue = "/admin/employees") String returnUrl) {
        Employee employee = employeeService.getById(id);


        model.addAttribute("returnUrl", returnUrl);

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

        log.info("Job assignment requested employeeId={} jobType={}",
                id, job.getTur());

        jobService.assignJobToEmployee(id, job);

        log.info("Job assigned successfully employeeId={}", id);

        return "redirect:/admin/employees/" + id;
    }

    @GetMapping("/jobs-overview")
    public String jobsOverview(Model model) {

        log.info("Jobs overview viewed");

        LocalDate week1Monday =
                availabilityService.getNextWeekMonday();
        LocalDate week2Monday =
                week1Monday.plusWeeks(1);

        model.addAttribute("hours",
                availabilityService.getHours());

        model.addAttribute("week1Dates",
                availabilityService.getWeekDates(week1Monday));
        model.addAttribute("week2Dates",
                availabilityService.getWeekDates(week2Monday));

        model.addAttribute("week1JobMap",
                jobService.getJobOverlapForWeek(week1Monday));

        model.addAttribute("week2JobMap",
                jobService.getJobOverlapForWeek(week2Monday));

        model.addAttribute("jobs",
                mevcutIsRepository.findAll());

        return "admin/admin-jobs-overview";
    }

    @GetMapping("/completed-jobs")
    public String completedJobs(Model model) {

        log.info("Completed jobs viewed");

        model.addAttribute("completedJobs",
                jobService.getAllTamamlananIsler());

        return "admin/admin-completed-jobs";
    }

    // Employee Info Update

    @GetMapping("/employees/{id}/edit")
    public String editEmployeeForm(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "/admin/all-employees") String returnUrl,
            Model model
    ) {
        Employee employee = employeeService.getById(id);

        AdminEmployeeUpdateDto dto = new AdminEmployeeUpdateDto();
        dto.setUsername(employee.getUsername());
        dto.setMinDay(employee.getMinDay());
        dto.setMinHour(employee.getMinHour());

        model.addAttribute("employee", employee);
        model.addAttribute("dto", dto);
        model.addAttribute("returnUrl", returnUrl);

        return "admin/admin-employee-edit";
    }

    @PostMapping("/employees/{id}/edit")
    public String updateEmployee(
            @PathVariable Long id,
            @ModelAttribute("dto") AdminEmployeeUpdateDto dto,
            @RequestParam(required = false, defaultValue = "/admin/all-employees") String returnUrl
    ) {
        employeeService.adminUpdateEmployee(id, dto);
        return "redirect:" + returnUrl;
    }

    @PostMapping("/settings/availability-sunday-only")
    @ResponseBody
    public void updateAvailabilityPolicy(@RequestParam boolean enabled) {

        systemSettingsService.updateAvailabilitySundayOnly(enabled);

        log.info("Availability Sunday-only policy updated: {}", enabled);
    }

}
