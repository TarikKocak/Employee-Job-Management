package com.webapp.demo_app.controller;

import com.webapp.demo_app.dto.AvailabilityValidationResult;
import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.model.MevcutIs;
import com.webapp.demo_app.security.SecurityUser;
import com.webapp.demo_app.service.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final JobService jobService;
    private final AvailabilityService availabilityService;
    private final EmployeeService employeeService;
    private final AvailabilityPolicyService policyService;

    public EmployeeController(JobService jobService,
                              AvailabilityService availabilityService,
                              EmployeeService employeeService,
                              AvailabilityPolicyService policyService) {
        this.jobService = jobService;
        this.availabilityService = availabilityService;
        this.employeeService = employeeService;
        this.policyService = policyService;
    }

    // ======================
    // SECURITY GUARD
    // ======================
    private void verifyEmployeeOwnership(Long employeeId,
                                         Authentication authentication) {

        if (authentication == null) {
            log.warn("Unauthorized access: no authentication");
            throw new AccessDeniedException("Not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof SecurityUser user)) {
            log.warn("Unauthorized access: invalid principal");
            throw new AccessDeniedException("Invalid authentication");
        }

        if (!Objects.equals(user.getId(), employeeId)) {
            log.warn("Unauthorized employee access attempt: requestedEmployeeId={}", employeeId);
            throw new AccessDeniedException("Unauthorized access");
        }
    }

    // ======================
    // DASHBOARD
    // ======================
    @GetMapping("/{employeeId}/dashboard")
    public String dashboard(@PathVariable Long employeeId,
                            Authentication authentication,
                            Model model) {

        verifyEmployeeOwnership(employeeId, authentication);

        log.info("Employee dashboard accessed");

        Employee employee = employeeService.getById(employeeId);
        model.addAttribute("employeeName", employee.getUsername());
        model.addAttribute("employeeId", employeeId);

        return "employee/home-dashboard";
    }

    // ======================
    // CURRENT JOBS
    // ======================
    @GetMapping("/{employeeId}/current-jobs")
    public String currentJobs(@PathVariable Long employeeId,
                              Authentication authentication,
                              @ModelAttribute("errorMessage") String errorMessage,
                              Model model) {

        verifyEmployeeOwnership(employeeId, authentication);

        log.info("Current jobs viewed");

        model.addAttribute("jobs",
                jobService.getMevcutIsler(employeeId));
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("errorMessage", errorMessage);

        return "employee/current-jobs";
    }

    @GetMapping("/{employeeId}/current-jobs/{jobId}/edit")
    public String editCurrentJob(@PathVariable Long employeeId,
                                 @PathVariable Long jobId,
                                 Authentication authentication,
                                 Model model) {

        verifyEmployeeOwnership(employeeId, authentication);

        MevcutIs job = jobService.getMevcutIsById(jobId);

        // 🔒 EXTRA SAFETY: job must belong to employee
        if (!job.getEmployee().getId().equals(employeeId)) {
            throw new AccessDeniedException("Unauthorized job access");
        }

        model.addAttribute("job", job);
        model.addAttribute("employeeId", employeeId);

        return "employee/edit-current-job";
    }

    @PostMapping("/{employeeId}/current-jobs/{jobId}/edit")
    public String saveCurrentJob(@PathVariable Long employeeId,
                                 @PathVariable Long jobId,
                                 Authentication authentication,
                                 @RequestParam Double sure,
                                 @RequestParam Integer bahsis,
                                 @RequestParam Boolean kartVerildi,
                                 @RequestParam Boolean yorumKartiVerildi,
                                 @RequestParam Boolean fotoAtildi) {

        verifyEmployeeOwnership(employeeId, authentication);

        log.info("Current job edited jobId={}", jobId);

        jobService.updateWriteOnlyFields(
                jobId, sure, bahsis, kartVerildi,
                yorumKartiVerildi, fotoAtildi
        );

        return "redirect:/employees/" + employeeId + "/current-jobs";
    }

    @PostMapping("/{employeeId}/current-jobs/{jobId}/submit")
    public String submitJob(@PathVariable Long employeeId,
                            @PathVariable Long jobId,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {

        verifyEmployeeOwnership(employeeId, authentication);

        log.info("Job submission requested jobId={}", jobId);

        try {
            jobService.submitJob(employeeId, jobId);
            log.info("Job submitted successfully jobId={}", jobId);

        } catch (IncompleteJobException e) {
            log.warn("Job submission failed jobId={} reason={}", jobId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employees/" + employeeId + "/current-jobs";
    }

    // ======================
    // COMPLETED JOBS
    // ======================
    @GetMapping("/{employeeId}/completed-jobs")
    public String completedJobs(@PathVariable Long employeeId,
                                Authentication authentication,
                                Model model) {

        verifyEmployeeOwnership(employeeId, authentication);

        model.addAttribute("jobs",
                jobService.getTamamlananIsler(employeeId));
        model.addAttribute("employeeId", employeeId);

        return "employee/completed-jobs";
    }

    // ======================
    // AVAILABILITY
    // ======================
    @GetMapping("/{employeeId}/availability")
    public String availability(@PathVariable Long employeeId,
                               Authentication authentication,
                               Model model) {

        verifyEmployeeOwnership(employeeId, authentication);

        LocalDate week1 = availabilityService.getNextWeekMonday();
        LocalDate week2 = week1.plusWeeks(1);

        boolean isSunday = policyService.isSunday();

        model.addAttribute("employeeId", employeeId);
        model.addAttribute("hours", availabilityService.getHours());
        model.addAttribute("week1Dates",
                availabilityService.getWeekDates(week1));
        model.addAttribute("week2Dates",
                availabilityService.getWeekDates(week2));

        model.addAttribute("week1StatusMap",
                availabilityService.buildStatusMap(
                        availabilityService.getWeekSlots(employeeId, week1)));

        model.addAttribute("week2StatusMap",
                availabilityService.buildStatusMap(
                        availabilityService.getWeekSlots(employeeId, week2)));

        model.addAttribute("isSunday", isSunday);

        return "employee/availability";
    }

    @PostMapping("/{employeeId}/availability/submit")
    @ResponseBody
    public ResponseEntity<?> submitAvailability(@PathVariable Long employeeId,
                                                Authentication authentication,
                                                @RequestParam("slots") String slotsRaw) {

        verifyEmployeeOwnership(employeeId, authentication);

        log.info("Availability submission requested");


        try {
            Set<String> selectedSlots = Arrays.stream(slotsRaw.split(","))
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toSet());

            AvailabilityValidationResult result =
                    availabilityService.validateMinimumAvailabilityPerWeek(
                            employeeId, selectedSlots
                    );

            if (!result.valid()) {
                log.warn("Availability validation failed: {}", result.message());
                return ResponseEntity.badRequest()
                        .body(result.message());
            }

            availabilityService.saveAvailabilityForWeek(
                    employeeId, selectedSlots);
            log.info("Availability saved successfully");

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Availability submission failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error.");
        }
    }
}
