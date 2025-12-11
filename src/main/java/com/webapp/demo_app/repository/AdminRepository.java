package com.webapp.demo_app.repository;

import com.webapp.demo_app.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

// for login in future

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
}
