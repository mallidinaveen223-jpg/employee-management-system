
package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.activation.service.AccountActivationService;
import com.naveen.employeemanagement.dto.PendingRegistrationResponse;
import com.naveen.employeemanagement.dto.RegistrationResult;
import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.entity.RegistrationRequest;
import com.naveen.employeemanagement.entity.RegistrationStatus;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import com.naveen.employeemanagement.repository.RegistrationRequestRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRequestRepository registrationRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeCodeGeneratorService employeeCodeGeneratorService;
    private final AccountActivationService accountActivationService;

    public RegistrationService(
            RegistrationRequestRepository registrationRequestRepository,
            EmployeeRepository employeeRepository,
            EmployeeCodeGeneratorService employeeCodeGeneratorService,
            AccountActivationService accountActivationService) {

        this.registrationRequestRepository = registrationRequestRepository;
        this.employeeRepository = employeeRepository;
        this.employeeCodeGeneratorService = employeeCodeGeneratorService;
        this.accountActivationService = accountActivationService;
    }

    // Step 1: Register employee and generate activation token
    @Transactional
    public RegistrationResult register(RegistrationRequest request) {

        if (registrationRequestRepository.existsByEmail(request.getEmail())
                || employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (registrationRequestRepository.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException("Mobile number already exists");
        }

        request.setPasswordHash(null);
        request.setStatus(RegistrationStatus.PENDING_ACTIVATION);

        RegistrationRequest savedRequest =
                registrationRequestRepository.save(request);

        String activationToken =
                accountActivationService.generateActivationToken(
                        savedRequest.getEmail());

        return new RegistrationResult(savedRequest, activationToken);
    }

    // Step 2: Admin approves the already activated employee
    @Transactional
    public Employee approveRegistration(
            Long registrationId,
            String department,
            String designation,
            Double salary,
            LocalDateTime joiningDate) {

        RegistrationRequest request = registrationRequestRepository
                .findById(registrationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Registration request not found"));

        if (request.getStatus() != RegistrationStatus.PENDING_APPROVAL) {
            throw new IllegalArgumentException(
                    "Registration is not waiting for approval");
        }

        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Activated employee account not found"));

        if (!"PENDING_APPROVAL".equals(employee.getEmploymentStatus())) {
            throw new IllegalArgumentException(
                    "Employee is not waiting for approval");
        }

        employee.setEmployeeCode(
                employeeCodeGeneratorService.generateEmployeeCode());

        employee.setDepartment(department);
        employee.setDesignation(designation);
        employee.setSalary(salary);
        employee.setJoiningDate(joiningDate);
        employee.setApprovedAt(LocalDateTime.now());
        employee.setEmploymentStatus("ACTIVE");

        Employee savedEmployee = employeeRepository.save(employee);

        request.setStatus(RegistrationStatus.APPROVED);
        registrationRequestRepository.save(request);

        return savedEmployee;
    }

    // Step 3: Admin rejects an activated employee
    @Transactional
    public RegistrationRequest rejectRegistration(Long registrationId) {

        RegistrationRequest request = registrationRequestRepository
                .findById(registrationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Registration request not found"));

        if (request.getStatus() != RegistrationStatus.PENDING_APPROVAL) {
            throw new IllegalArgumentException(
                    "Registration is not waiting for approval");
        }

        employeeRepository.findByEmail(request.getEmail())
                .ifPresent(employee -> {
                    employee.setEmploymentStatus("REJECTED");
                    employeeRepository.save(employee);
                });

        request.setStatus(RegistrationStatus.REJECTED);

        return registrationRequestRepository.save(request);
    }

    // Get employees waiting for Admin approval
    public List<PendingRegistrationResponse> getPendingRegistrations() {

        return registrationRequestRepository
                .findByStatus(RegistrationStatus.PENDING_APPROVAL)
                .stream()
                .map(request -> new PendingRegistrationResponse(
                        request.getId(),
                        request.getFullName(),
                        request.getEmail(),
                        request.getMobile(),
                        request.getStatus().name(),
                        request.getCreatedAt()
                ))
                .toList();
    }
}
