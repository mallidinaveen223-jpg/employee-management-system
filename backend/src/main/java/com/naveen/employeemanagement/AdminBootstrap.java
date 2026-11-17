package com.naveen.employeemanagement;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import com.naveen.employeemanagement.service.AdminSetupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AdminBootstrap.class);

    private final AdminSetupService adminSetupService;
    private final EmployeeRepository employeeRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.super-admin.migration-enabled:false}")
    private boolean superAdminMigrationEnabled;

    private final String adminName = "Mallidi Naveen Kumar";
    private final String adminMobile = "7416676272";

    public AdminBootstrap(
            AdminSetupService adminSetupService,
            EmployeeRepository employeeRepository) {

        this.adminSetupService = adminSetupService;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (superAdminMigrationEnabled) {
            migrateConfiguredAdmin();
            return;
        }

        boolean adminExists =
                Boolean.TRUE.equals(
                        employeeRepository.existsByRole("ADMIN")
                );

        boolean superAdminExists =
                Boolean.TRUE.equals(
                        employeeRepository.existsByRole("SUPER_ADMIN")
                );

        if (adminExists || superAdminExists) {
            LOGGER.info("Admin account already exists. Skipping setup.");
            return;
        }

        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {

            throw new IllegalStateException(
                    "Admin email and password must be configured."
            );
        }

        adminSetupService.createAdmin(
                adminName,
                adminEmail.trim(),
                adminMobile,
                adminPassword
        );

        LOGGER.info("Initial ADMIN account created successfully.");
    }

    private void migrateConfiguredAdmin() {

        if (adminEmail == null || adminEmail.isBlank()) {
            throw new IllegalStateException(
                    "Admin email is required for SUPER_ADMIN migration."
            );
        }

        Employee owner = employeeRepository
                .findByEmail(adminEmail.trim())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Configured admin account was not found. "
                                        + "Migration stopped."
                        )
                );

        if ("SUPER_ADMIN".equals(owner.getRole())) {
            LOGGER.info(
                    "Configured account is already SUPER_ADMIN. "
                            + "No migration needed."
            );
            return;
        }

        if (Boolean.TRUE.equals(
                employeeRepository.existsByRole("SUPER_ADMIN"))) {

            throw new IllegalStateException(
                    "Another SUPER_ADMIN already exists. Migration stopped."
            );
        }

        if (!"ADMIN".equals(owner.getRole())) {
            throw new IllegalStateException(
                    "Only an existing ADMIN can be migrated "
                            + "using this startup configuration."
            );
        }

        owner.setRole("SUPER_ADMIN");
        employeeRepository.save(owner);

        LOGGER.info(
                "Configured ADMIN migrated to SUPER_ADMIN successfully."
        );
    }
}