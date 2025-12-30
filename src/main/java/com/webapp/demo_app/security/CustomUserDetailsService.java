package com.webapp.demo_app.security;

import com.webapp.demo_app.repository.AdminRepository;
import com.webapp.demo_app.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(AdminRepository adminRepository,
                                    EmployeeRepository employeeRepository) {
        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return adminRepository.findByUsername(username)
                .map(a -> new SecurityUser(
                        a.getId(),
                        a.getUsername(),
                        a.getPassword(),
                        "ROLE_ADMIN"
                ))
                .orElseGet(() ->
                        employeeRepository.findByUsername(username)
                                .map(e -> new SecurityUser(
                                        e.getId(),
                                        e.getUsername(),
                                        e.getPassword(),
                                        "ROLE_EMPLOYEE"
                                ))
                                .orElseThrow(() ->
                                        new UsernameNotFoundException("User not found"))
                );
    }
}

