package com.webapp.demo_app.repository;

import com.webapp.demo_app.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// for login in future

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
    boolean existsByUsername(String username);
}
