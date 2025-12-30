package com.webapp.demo_app.config;

import com.webapp.demo_app.model.Admin;
import com.webapp.demo_app.repository.AdminRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {

    // ---------------------------------------------------------------------------------------
    // Assuming there is no admin entity in DB this method will create a initial admin for you
    // ---------------------------------------------------------------------------------------
    @Bean
    CommandLineRunner createInitialAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String defaultUsername = "admin";
            String defaultPassword = "admin123";

            if (adminRepository.existsByUsername(defaultUsername)) {
                return;
            }

            Admin admin = new Admin();
            admin.setUsername(defaultUsername);
            admin.setPassword(passwordEncoder.encode(defaultPassword));

            adminRepository.save(admin);

            System.out.println(" Initial admin created");
            System.out.println(" username: admin");
            System.out.println(" password: admin123");
        };
    }
}
