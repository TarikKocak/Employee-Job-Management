package com.webapp.demo_app.service;


import com.webapp.demo_app.model.Admin;
import com.webapp.demo_app.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // READ OPERATIONS
    // =========================
    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Admin not found: " + id));
    }

    // =========================
    // WRITE OPERATIONS
    // =========================
    @Transactional
    public void updatePassword(Long adminId, String rawPassword) {
        Admin admin = getById(adminId);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        adminRepository.save(admin);
    }

    @Transactional
    public Admin save(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }
}