package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.entity.RegistrationRequest;
import com.naveen.employeemanagement.entity.RegistrationStatus;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import com.naveen.employeemanagement.repository.RegistrationRequestRepository;
import com.naveen.employeemanagement.dto.PendingRegistrationResponse;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRequestRepository registrationRequestRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    public RegistrationService(
            RegistrationRequestRepository registrationRequestRepository, EmployeeRepository employeeRepository) {

        this.registrationRequestRepository = registrationRequestRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public RegistrationRequest register(RegistrationRequest request) {

        if (registrationRequestRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (registrationRequestRepository.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException("Mobile number already exists");
        }

        request.setPasswordHash(
                passwordEncoder.encode(request.getPasswordHash())
        );

        request.setStatus(RegistrationStatus.PENDING);

        return registrationRequestRepository.save(request);
    }

    @Transactional
    public Employee approveRegistration(
            Long registrationId,
            String employeeCode,
            String department,
            String designation,
            Double salary,
            LocalDateTime joiningDate) {

        RegistrationRequest request = registrationRequestRepository
                .findById(registrationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Registration request not found"));

        if (request.getStatus() != RegistrationStatus.PENDING) {
            throw new IllegalArgumentException("Registration request is not pending");
        }

        if (employeeRepository.existsByEmployeeCode(employeeCode)) {
            throw new IllegalArgumentException("Employee code already exists");
        }

        Employee employee = new Employee();
        employee.setName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setPasswordHash(request.getPasswordHash());
        employee.setMobile(request.getMobile());
        employee.setEmployeeCode(employeeCode);
        employee.setDepartment(department);
        employee.setDesignation(designation);
        employee.setRole("EMPLOYEE");
        employee.setSalary(salary);
        employee.setJoiningDate(joiningDate);
        employee.setEmploymentStatus("ACTIVE");

        Employee savedEmployee = employeeRepository.save(employee);

        request.setStatus(RegistrationStatus.APPROVED);

        registrationRequestRepository.save(request);

        return savedEmployee;
    }

    @Transactional
    public RegistrationRequest rejectRegistration(Long registrationId) {
        RegistrationRequest request = registrationRequestRepository
                .findById(registrationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Registration request not found"));

        if (request.getStatus() != RegistrationStatus.PENDING) {
            throw new IllegalArgumentException("Registration request is not pending");
        }

        request.setStatus(RegistrationStatus.REJECTED);

        return registrationRequestRepository.save(request);
    }

    public List<PendingRegistrationResponse> getPendingRegistrations() {
        return registrationRequestRepository.findByStatus(RegistrationStatus.PENDING)
                .stream()
                .map(request -> new PendingRegistrationResponse(
                        request.getId(),
                        request.getFullName(),
                        request.getEmail(),
                        request.getMobile(),
                        request.getStatus().toString(),
                        request.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}