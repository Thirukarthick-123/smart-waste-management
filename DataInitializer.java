package com.example.smartwaste; // <-- this matches the root package

import com.example.smartwaste.model.Admin;
import com.example.smartwaste.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Creates a default admin user on first start‑up.
 * If you already have a root login you can keep the admin = root,
 * but we also keep a separate "admin" user for the JWT flow.
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // If the admin row does not exist, create it.
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin admin = Admin.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123")) // BCrypt‑encoded
                    .role("ROLE_ADMIN")
                    .build();
            adminRepository.save(admin);
            System.out.println(">>> Default admin created – username=admin / password=admin123");
        }
    }
}
