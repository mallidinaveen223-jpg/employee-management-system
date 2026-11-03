
package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.entity.PasswordResetOtp;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import com.naveen.employeemanagement.repository.PasswordResetOtpRepository;
import com.naveen.employeemanagement.security.JwtService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private final PasswordResetOtpRepository otpRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.dev-mode:false}")
    private boolean otpDevMode;

    public PasswordResetService(
            PasswordResetOtpRepository otpRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.otpRepository = otpRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Generate OTP
    @Transactional
    public void generateOtp(String email, String mobile) {

        employeeRepository.findByEmail(email)
                .filter(employee -> employee.getMobile().equals(mobile))
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or mobile number"));

        String otp = String.format("%06d",
                secureRandom.nextInt(1_000_000));

        PasswordResetOtp resetOtp = otpRepository.findByEmail(email)
                .orElse(null);

        if(resetOtp !=null && resetOtp.getCreatedAt() != null &&
        resetOtp.getCreatedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Please wait for 60 seconds before requesting a new OTP");
        }
        if(resetOtp == null){
            resetOtp = new PasswordResetOtp();
        }

        resetOtp.setEmail(email);
        resetOtp.setOtpHash(passwordEncoder.encode(otp));
        resetOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        resetOtp.setAttempts(0);
        resetOtp.setVerified(false);
        resetOtp.setCreatedAt(LocalDateTime.now());

        otpRepository.save(resetOtp);

        if (otpDevMode) {
            System.out.println("Development OTP for " + email + ": " + otp);
        }
    }

    // Verify OTP and generate Password Reset Token
    public String verifyOtp(String email, String otp) {

        PasswordResetOtp resetOtp = otpRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid OTP request"));

        if (resetOtp.isVerified()) {
            throw new IllegalArgumentException("OTP already used");
        }

        if (resetOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired");
        }

        if (resetOtp.getAttempts() >= 3) {
            throw new IllegalArgumentException(
                    "Maximum OTP attempts exceeded");
        }

        if (!passwordEncoder.matches(otp, resetOtp.getOtpHash())) {

            resetOtp.setAttempts(resetOtp.getAttempts() + 1);
            otpRepository.save(resetOtp);

            throw new IllegalArgumentException("Invalid OTP");
        }

        resetOtp.setVerified(true);
        otpRepository.save(resetOtp);

        return jwtService.generatePasswordResetToken(email);
    }

    // Reset Password using Reset Token
    @Transactional
    public void resetPassword(
            String resetToken,
            String newPassword,
            String confirmPassword) {

        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException("Reset token is required");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords don't match");
        }

        String email;

        if (!jwtService.isPasswordResetTokenValid(
                resetToken, getTokenEmail(resetToken))) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        email = jwtService.extractEmail(resetToken);

        PasswordResetOtp resetOtp = otpRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid password reset request"));

        if (!resetOtp.isVerified()) {
            throw new IllegalArgumentException("Please verify OTP first");
        }

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found"));

        employee.setPasswordHash(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);

        // Delete OTP record so the reset token cannot be reused
        otpRepository.deleteByEmail(email);
    }

    private String getTokenEmail(String resetToken) {
        try {
            return jwtService.extractEmail(resetToken);
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Invalid or expired reset token");
        }
    }
}
