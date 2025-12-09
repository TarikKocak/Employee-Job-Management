package com.webapp.demo_app.controller;

import com.webapp.demo_app.model.AvailabilitySlot;
import com.webapp.demo_app.model.MevcutIs;
import com.webapp.demo_app.service.AvailabilityService;
import com.webapp.demo_app.service.IncompleteJobException;
import com.webapp.demo_app.service.JobService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final JobService jobService;

    private final AvailabilityService availabilityService;

    public EmployeeController(JobService jobService, AvailabilityService availabilityService) {
        this.jobService = jobService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/home")
    public String homeRedirect(@RequestParam Long employeeId) {
        return "redirect:/employees/" + employeeId + "/home-dashboard";
    }

    @GetMapping("/{employeeId}/dashboard")
    public String dashboard(@PathVariable Long employeeId, Model model) {
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

        // NEW: Haftalar kaydıysa eski kayıtları ileri taşı
        availabilityService.rollWeeksIfNeeded(employeeId);

        // NEW: 1. hafta (haftaya) ve 2. hafta (ondan sonraki hafta) için tarihleri hesapla
        LocalDate week1Monday = availabilityService.getNextWeekMonday();       // 1. hafta (haftaya)
        LocalDate week2Monday = week1Monday.plusWeeks(1);                      // 2. hafta

        List<LocalDate> week1Dates = availabilityService.getWeekDates(week1Monday);
        List<LocalDate> week2Dates = availabilityService.getWeekDates(week2Monday);

        List<AvailabilitySlot> week1Slots = availabilityService.getWeekSlots(employeeId, week1Monday);
        List<AvailabilitySlot> week2Slots = availabilityService.getWeekSlots(employeeId, week2Monday);

        Map<String, Integer> week1StatusMap = availabilityService.buildStatusMap(week1Slots);
        Map<String, Integer> week2StatusMap = availabilityService.buildStatusMap(week2Slots);

        model.addAttribute("employeeId", employeeId);
        model.addAttribute("hours", availabilityService.getHours());

        model.addAttribute("week1Dates", week1Dates);
        model.addAttribute("week2Dates", week2Dates);
        model.addAttribute("week1StatusMap", week1StatusMap);
        model.addAttribute("week2StatusMap", week2StatusMap);

        return "availability"; // NEW: availability.html
    }


    // NEW: Endpoint that toggles availability when the cell is clicked (AJAX)
    @PostMapping("/{employeeId}/availability/toggle")
    @ResponseBody
    public ResponseEntity<String> toggleAvailability(@PathVariable Long employeeId,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                     LocalDate date,
                                                     @RequestParam Integer hour) {
        availabilityService.toggleSlot(employeeId, date, hour);
        return ResponseEntity.ok("OK");
    }
}
