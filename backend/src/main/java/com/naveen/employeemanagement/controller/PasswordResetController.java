package com.naveen.employeemanagement.controller;

import com.naveen.employeemanagement.dto.ForgotPasswordRequest;
import com.naveen.employeemanagement.dto.ResetPasswordRequest;
import com.naveen.employeemanagement.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        passwordResetService.generateOtp(request.getEmail(), request.getMobile());
        return ResponseEntity.ok("If the account details are valid, an OTP will be sent to your registered mobile number.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp){

        String resetToken= passwordResetService.verifyOtp(email, otp);
        return ResponseEntity.ok(Map.of("message",
                "OTP verified successfully.", "resetToken", resetToken));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid
            @RequestBody ResetPasswordRequest request){

        passwordResetService.resetPassword(
                request.getResetToken(),
                request.getNewPassword(),
                request.getConfirmPassword());

        return  ResponseEntity.ok("Password reset successfully.");
    }
}
