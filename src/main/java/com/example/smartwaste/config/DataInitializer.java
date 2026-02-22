package com.example.smartwaste.config;

import com.example.smartwaste.model.Admin;
import com.example.smartwaste.model.Bin;
import com.example.smartwaste.model.BinStatus;
import com.example.smartwaste.repository.AdminRepository;
import com.example.smartwaste.repository.BinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final BinRepository binRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Initialize Admin
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin admin = Admin.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ROLE_ADMIN")
                    .build();
            adminRepository.save(admin);
            System.out.println("✅ Default admin created: admin / admin123");
        }

        // 1b. Initialize Worker
        if (adminRepository.findByUsername("worker").isEmpty()) {
            Admin worker = Admin.builder()
                    .username("worker")
                    .password(passwordEncoder.encode("worker123"))
                    .role("ROLE_WORKER")
                    .build();
            adminRepository.save(worker);
            System.out.println("✅ Default worker created: worker / worker123");
        }

        // 2. Initialize Bins
        initializeBin("BIN-001", 28.7041, 77.1025, 25, BinStatus.OK);
        initializeBin("BIN-002", 28.6139, 77.2090, 85, BinStatus.NEAR_FULL);
        initializeBin("BIN-003", 28.6129, 77.2295, 95, BinStatus.FULL);
        initializeBin("BIN-004", 28.5355, 77.3910, 60, BinStatus.OK);
        initializeBin("BIN-005", 28.4595, 77.0266, 72, BinStatus.NEAR_FULL);
        initializeBin("BIN-WOKWI-01", 28.6129, 77.2295, 0, BinStatus.OK);
        
        System.out.println("✅ Database initialization complete.");
    }

    private void initializeBin(String binId, double lat, double lng, int fill, BinStatus status) {
        if (binRepository.findByBinId(binId).isEmpty()) {
            Bin bin = Bin.builder()
                    .binId(binId)
                    .lat(lat)
                    .lng(lng)
                    .fill(fill)
                    .status(status)
                    .lastUpdate(LocalDateTime.now())
                    .build();
            binRepository.save(bin);
            System.out.println("📦 Bin initialized: " + binId);
        }
    }
}
