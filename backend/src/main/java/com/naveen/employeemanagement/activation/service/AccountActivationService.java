package com.naveen.employeemanagement.activation.service;

import com.naveen.employeemanagement.activation.entity.ActivationToken;
import com.naveen.employeemanagement.activation.repository.ActivationTokenRepository;
import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.entity.RegistrationRequest;
import com.naveen.employeemanagement.entity.RegistrationStatus;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import com.naveen.employeemanagement.repository.RegistrationRequestRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AccountActivationService {

    private final ActivationTokenRepository activationTokenRepository;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public AccountActivationService(
            ActivationTokenRepository activationTokenRepository,
            RegistrationRequestRepository registrationRequestRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder){

        this.activationTokenRepository = activationTokenRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String generateActivationToken(String email){

        RegistrationRequest request = registrationRequestRepository
                .findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Registration Request Not Found"));

        if (request.getStatus() != RegistrationStatus.PENDING_ACTIVATION){
            throw new IllegalArgumentException("Registration is Not Pending activation");
        }

        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);

        activationTokenRepository.deleteByEmail(email);

        ActivationToken activationToken = new ActivationToken();
        activationToken.setEmail(email);
        activationToken.setTokenHash(hashToken(rawToken));
        activationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        activationTokenRepository.save(activationToken);

        return rawToken;
    }

    @Transactional
    public Employee activateAccount(
            String rawToken,
            String password,
            String confirmPassword){

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Activation token is required");
        }

        if (password == null || password.length() <  8){
            throw  new IllegalArgumentException("Password must contain at least 8 characters");
        }

        if (!password.equals(confirmPassword)){
            throw new IllegalArgumentException("Passwords do not match");
        }

        ActivationToken activationToken = activationTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid activation link"));

        if (activationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            activationTokenRepository.delete(activationToken);
            throw new IllegalStateException("Activation Token has expired");
        }

        RegistrationRequest request = registrationRequestRepository
                .findByEmail(activationToken.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("REgistration request not found"));

        if (request.getStatus() != RegistrationStatus.PENDING_ACTIVATION){
            throw new IllegalArgumentException("Registration is not pending activation");
        }

        if (employeeRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalArgumentException("Employee account already exists");
        }

        Employee employee = new Employee();
        employee.setName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setMobile(request.getMobile());
        employee.setPasswordHash(passwordEncoder.encode(password));
        employee.setDepartment("UNASSIGNED");
        employee.setRole("EMPLOYEE");
        employee.setEmploymentStatus("PENDING_APPROVAL");


        Employee savedEmployee = employeeRepository.save(employee);

        request.setStatus(RegistrationStatus.PENDING_APPROVAL);
        registrationRequestRepository.save(request);

        activationTokenRepository.delete(activationToken);

        return savedEmployee;
    }
    private String hashToken(String token){

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);
        } catch (Exception exception){
            throw new IllegalArgumentException("Unable to hash activation token", exception);
        }
    }
}
