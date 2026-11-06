package com.naveen.employeemanagement;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import com.naveen.employeemanagement.service.AdminSetupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private final AdminSetupService adminSetupService;
    private final EmployeeRepository employeeRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value ("${app.admin.password}")
    private String adminPassword;

    private final String adminName = "Mallidi Naveen Kumar";
    private final String adminMobile = "7416676272";

    public AdminBootstrap(
            AdminSetupService adminSetupService,
            EmployeeRepository employeeRepository) {
        this.adminSetupService = adminSetupService;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(ApplicationArguments args){

        if(employeeRepository.existsByRole("ADMIN")){
            System.out.println("Admin already exists. Skipping setup.");
            return;
        }

        if(adminEmail == null || adminEmail.isBlank()
            || adminPassword == null || adminPassword.isBlank()){
            throw new IllegalStateException(
                    "Admin email and password must be configured.");
        }

        Employee admin = adminSetupService.createAdmin(
                adminName,
                adminEmail,
                adminMobile,
                adminPassword
        );

        System.out.println("" +
                "Initial Admin account created successfully: "
                + admin.getEmail());
    }

}
