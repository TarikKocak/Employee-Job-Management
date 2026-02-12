package com.webapp.demo_app.controller;

import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.security.SecurityUser;
import com.webapp.demo_app.service.AdminService;
import com.webapp.demo_app.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
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

        log.info("Settings page accessed");

        if ("ROLE_EMPLOYEE".equals(user.getRole())) {
            Employee employee = employeeService.getById(user.getId());
            model.addAttribute("email", employee.getEmail());
        }

        model.addAttribute("username", user.getUsername());
        return "settings/settings";
    }

    // ======================
    // CHANGE PASSWORD
    // ======================
    @PostMapping("/change-password")
    public String changePassword(Authentication authentication,
                                 @RequestParam(required = false) String newPassword,
                                 @RequestParam(required = false) String confirmPassword,
                                 @RequestParam(required = false) String email,
                                 RedirectAttributes redirectAttributes) {

        SecurityUser user =
                (SecurityUser) authentication.getPrincipal();

        log.info("Settings update requested");

        boolean passwordProvided = newPassword != null && !newPassword.isBlank();
        boolean confirmProvided = confirmPassword != null && !confirmPassword.isBlank();
        boolean emailProvided = email != null && !email.isBlank();

        if (passwordProvided || confirmProvided) {
            if (newPassword == null || !newPassword.equals(confirmPassword)) {
                log.warn("Settings update failed: passwords do not match");
                redirectAttributes.addFlashAttribute(
                        "popupError",
                        "Passwords do not match !"
                );
                return "redirect:/settings";
            }

            if ("ROLE_EMPLOYEE".equals(user.getRole())) {
                employeeService.updatePassword(user.getId(), newPassword);
                log.info("Employee password updated");
            } else if ("ROLE_ADMIN".equals(user.getRole())) {
                adminService.updatePassword(user.getId(), newPassword);
                log.info("Admin password updated");
            }
        }

        if ("ROLE_EMPLOYEE".equals(user.getRole()) && emailProvided) {
            employeeService.updateEmail(user.getId(), email);
            log.info("Employee email updated");
        }

        if (!passwordProvided && !confirmProvided && !("ROLE_EMPLOYEE".equals(user.getRole()) && emailProvided)){
            redirectAttributes.addFlashAttribute(
                    "popupError",
                    "Please enter at least one value to update !"
            );
            return "redirect:/settings";
        }


        redirectAttributes.addFlashAttribute(
                "popupSuccess",
                "Settings updated successfully ✔"
        );

        return "redirect:/settings";
    }

    @PostMapping("/change-email")
    public String changeEmail(Authentication authentication,
                              @RequestParam String email,
                              RedirectAttributes redirectAttributes) {

        SecurityUser user =
                (SecurityUser) authentication.getPrincipal();

        if (!"ROLE_EMPLOYEE".equals(user.getRole())) {
            log.warn("Email change denied: user is not an employee");
            redirectAttributes.addFlashAttribute(
                    "popupError",
                    "Only employees can update email !"
            );
            return "redirect:/settings";
        }

        employeeService.updateEmail(user.getId(), email);

        log.info("Employee email updated");

        redirectAttributes.addFlashAttribute(
                "popupSuccess",
                "Email updated successfully ✔"
        );

        return "redirect:/settings";
    }
}
