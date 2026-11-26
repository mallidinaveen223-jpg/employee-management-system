package com.naveen.employeemanagement.activation.controller;

import com.naveen.employeemanagement.activation.dto.SetPasswordRequest;
import com.naveen.employeemanagement.activation.service.AccountActivationService;
import com.naveen.employeemanagement.entity.Employee;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AccountActivationController {

    private final AccountActivationService accountActivationService;

    public AccountActivationController(AccountActivationService accountActivationService) {

        this.accountActivationService = accountActivationService;
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateAccount(
            @Valid @RequestBody SetPasswordRequest request){

        try {
            Employee employee = accountActivationService.activateAccount(
                    request.getToken(),
                    request.getPassword(),
                    request.getConfirmPassword());

            return ResponseEntity.ok(Map.of(
                    "message", "Account activated successfully. Waiting for Admin Approval.",
                    "email", employee.getEmail(),
                    "status", employee.getEmploymentStatus()));
        } catch (IllegalArgumentException exception){

            return ResponseEntity.badRequest().body(
                    Map.of("message", exception.getMessage()));
        }
    }
}
