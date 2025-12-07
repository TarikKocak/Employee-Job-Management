package com.webapp.demo_app.controller;

import com.webapp.demo_app.model.MevcutIs;
import com.webapp.demo_app.service.IncompleteJobException;
import com.webapp.demo_app.service.JobService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final JobService jobService;

    public EmployeeController(JobService jobService) {
        this.jobService = jobService;
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
}
