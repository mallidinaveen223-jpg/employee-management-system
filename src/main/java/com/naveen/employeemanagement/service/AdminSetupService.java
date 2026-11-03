package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminSetupService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSetupService(
            EmployeeRepository employeeREpository,
            PasswordEncoder passwordEncoder) {

        this.employeeRepository = employeeREpository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public Employee createAdmin(
            String name,
            String email,
            String mobile,
            String password) {

        if (employeeRepository.findByEmail(email).isPresent()){
            throw new IllegalStateException("Email already exists");
        }
        if (employeeRepository.existsByEmployeeCode("ADMIN001")) {
            throw new IllegalStateException("Admin employee code already exists");
        }

        Employee admin = new Employee();

        admin.setName(name);
        admin.setEmail(email);
        admin.setMobile(mobile);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setEmployeeCode("ADMIN001");
        admin.setDepartment("Administration");
        admin.setDesignation("System Administrator");
        admin.setRole("ADMIN");
        admin.setSalary(0);

        return employeeRepository.save(admin);
    }
}
