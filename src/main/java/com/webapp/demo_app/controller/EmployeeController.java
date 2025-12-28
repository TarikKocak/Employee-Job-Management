package com.webapp.demo_app.controller;

import com.webapp.demo_app.dto.AvailabilityValidationResult;
import com.webapp.demo_app.model.AvailabilitySlot;
import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.model.MevcutIs;
import com.webapp.demo_app.repository.EmployeeRepository;
import com.webapp.demo_app.service.AvailabilityService;
import com.webapp.demo_app.service.EmployeeService;
import com.webapp.demo_app.service.IncompleteJobException;
import com.webapp.demo_app.service.JobService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final JobService jobService;

    private final AvailabilityService availabilityService;
    private final EmployeeService employeeService;

    public EmployeeController(JobService jobService, AvailabilityService availabilityService, EmployeeService employeeService) {
        this.jobService = jobService;
        this.availabilityService = availabilityService;
        this.employeeService = employeeService;
    }

    @GetMapping("/home")
    public String homeRedirect(@RequestParam Long employeeId) {
        return "redirect:/employees/" + employeeId + "/home-dashboard";
    }

    @GetMapping("/{employeeId}/dashboard")
    public String dashboard(@PathVariable Long employeeId, Model model) {

        Employee employee = employeeService.getById(employeeId);

        model.addAttribute("employeeName", employee.getName());
        model.addAttribute("employeeId", employeeId);
        return "home-dashboard";
    }

    @GetMapping("/{employeeId}/current-jobs")
    public String currentJobs(@PathVariable Long employeeId,
                              @ModelAttribute("errorMessage") String errorMessage,
                              Model model) {
        List<MevcutIs> jobs = jobService.getMevcutIsler(employeeId);
        model.addAttribute("jobs", jobs);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("errorMessage", errorMessage);
        return "current-jobs";
    }

    @GetMapping("/{employeeId}/current-jobs/{jobId}/edit")
    public String editCurrentJob(@PathVariable Long employeeId,
                                 @PathVariable Long jobId,
                                 Model model) {
        MevcutIs job = jobService.getMevcutIsById(jobId);
        model.addAttribute("job", job);
        model.addAttribute("employeeId", employeeId);
        return "edit-current-job";
    }

    @PostMapping("/{employeeId}/current-jobs/{jobId}/edit")
    public String saveCurrentJob(@PathVariable Long employeeId,
                                 @PathVariable Long jobId,
                                 @RequestParam Double sure,
                                 @RequestParam Integer bahsis,
                                 @RequestParam Boolean kartVerildi,
                                 @RequestParam Boolean yorumKartiVerildi,
                                 @RequestParam Boolean fotoAtildi) {

        jobService.updateWriteOnlyFields(jobId, sure, bahsis, kartVerildi, yorumKartiVerildi, fotoAtildi);
        return "redirect:/employees/" + employeeId + "/current-jobs";
    }

    @PostMapping("/{employeeId}/current-jobs/{jobId}/submit")
    public String submitJob(@PathVariable Long employeeId,
                            @PathVariable Long jobId,
                            RedirectAttributes redirectAttributes) {
        try {
            jobService.submitJob(employeeId, jobId);
        } catch (IncompleteJobException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employees/" + employeeId + "/current-jobs";
    }

    @GetMapping("/{employeeId}/completed-jobs")
    public String completedJobs(@PathVariable Long employeeId, Model model) {
        model.addAttribute("jobs", jobService.getTamamlananIsler(employeeId));
        model.addAttribute("employeeId", employeeId);
        return "completed-jobs";
    }


    // ======================
    // NEW: AVAILABILITY PART
    // ======================
    @GetMapping("/{employeeId}/availability")
    public String availability(@PathVariable Long employeeId, Model model) {

        LocalDate week1 = availabilityService.getNextWeekMonday();
        LocalDate week2 = week1.plusWeeks(1);

        model.addAttribute("employeeId", employeeId);
        model.addAttribute("hours", availabilityService.getHours());

        model.addAttribute("week1Dates", availabilityService.getWeekDates(week1));
        model.addAttribute("week2Dates", availabilityService.getWeekDates(week2));

        model.addAttribute("week1StatusMap",
                availabilityService.buildStatusMap(
                        availabilityService.getWeekSlots(employeeId, week1)));

        model.addAttribute("week2StatusMap",
                availabilityService.buildStatusMap(
                        availabilityService.getWeekSlots(employeeId, week2)));

        return "availability";
    }






    @PostMapping("/{employeeId}/availability/submit")
    @ResponseBody
    public ResponseEntity<?> submitAvailability(
            @PathVariable Long employeeId,
            @RequestParam("slots") String slotsRaw
    ) {
        try {
            Set<String> selectedSlots = Arrays.stream(slotsRaw.split(","))
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toSet());

            AvailabilityValidationResult result =
                    availabilityService.validateMinimumAvailabilityPerWeek(
                            employeeId,
                            selectedSlots
                    );

            if (!result.valid()) {
                return ResponseEntity
                        .badRequest()
                        .body(result.message());
            }

            availabilityService.saveAvailabilityForWeek(employeeId, selectedSlots);
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Beklenmeyen bir hata oluştu.");
        }
    }



}
