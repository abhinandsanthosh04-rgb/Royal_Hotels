package com.royalhotels.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * AdminInitializer - Controlled creation of the initial administrator account.
 *
 * Security Design:
 * 1. Executes strictly on backend startup.
 * 2. Checks if ANY admin exists in the database.
 * 3. Only if zero admin accounts exist, securely seeds the initial administrator.
 * 4. Password is read from environment variable ADMIN_INITIAL_PASSWORD / application.properties
 *    and hashed using BCrypt. Plain-text passwords are never stored in the database.
 * 5. No public registration endpoint exists anywhere in the application.
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.initial.username:admin}")
    private String initialUsername;

    @Value("${admin.initial.password:RoyalAdmin@2026}")
    private String initialPassword;

    @Value("${admin.initial.fullname:Royal Hotels Administrator}")
    private String initialFullName;

    @Override
    public void run(String... args) {
        try {
            long adminCount = adminRepository.count();
            if (adminCount == 0) {
                logger.info("[ADMIN SETUP] No admin account found. Creating initial administrator...");

                Admin admin = new Admin();
                admin.setUsername(initialUsername);
                admin.setPasswordHash(passwordEncoder.encode(initialPassword));
                admin.setFullName(initialFullName);
                admin.setRole("ROLE_ADMIN");

                adminRepository.save(admin);

                logger.info("============================================================");
                logger.info("[ADMIN SETUP] Initial admin created successfully!");
                logger.info("  Username : {}", initialUsername);
                logger.info("  Role     : ROLE_ADMIN");
                logger.info("  Full Name: {}", initialFullName);
                logger.info("  Password : Read from ADMIN_INITIAL_PASSWORD env/config");
                logger.info("============================================================");
            } else {
                logger.info("[ADMIN SETUP] Administrator account(s) already exist (count: {}). Seeding skipped.", adminCount);
            }
        } catch (Exception e) {
            logger.warn("[ADMIN SETUP] Could not verify/create initial admin on startup (Database may not be connected yet): {}", e.getMessage());
        }
    }
}
