package com.webapp.demo_app.controller;

import com.webapp.demo_app.security.SecurityUser;
import com.webapp.demo_app.service.AdminService;
import com.webapp.demo_app.service.EmployeeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final EmployeeService employeeService;
    private final AdminService adminService;

    public SettingsController(EmployeeService employeeService,
                              AdminService adminService) {
        this.employeeService = employeeService;
        this.adminService = adminService;
    }

    // ======================
    // VIEW SETTINGS PAGE
    // ======================
    @GetMapping
    public String settings(Authentication authentication, Model model) {

        SecurityUser user =
                (SecurityUser) authentication.getPrincipal();

        model.addAttribute("username", user.getUsername());
        return "settings/settings";
    }

    // ======================
    // CHANGE PASSWORD
    // ======================
    @PostMapping("/change-password")
    public String changePassword(Authentication authentication,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {

        SecurityUser user =
                (SecurityUser) authentication.getPrincipal();

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                    "popupError",
                    "Passwords do not match"
            );
            return "redirect:/settings";
        }

        if ("ROLE_EMPLOYEE".equals(user.getRole())) {
            employeeService.updatePassword(user.getId(), newPassword);
        } else if ("ROLE_ADMIN".equals(user.getRole())) {
            adminService.updatePassword(user.getId(), newPassword);
        }

        redirectAttributes.addFlashAttribute(
                "popupSuccess",
                "Password updated successfully ✔"
        );

        return "redirect:/settings";
    }
}
