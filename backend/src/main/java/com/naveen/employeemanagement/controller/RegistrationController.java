
package com.naveen.employeemanagement.controller;

import com.naveen.employeemanagement.activation.service.ActivationEmailService;
import com.naveen.employeemanagement.dto.RegistrationResult;
import com.naveen.employeemanagement.entity.RegistrationRequest;
import com.naveen.employeemanagement.service.RegistrationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class RegistrationController {

    private final RegistrationService registrationService;
    private final ActivationEmailService activationEmailService;

    public RegistrationController(
            RegistrationService registrationService,
            ActivationEmailService activationEmailService) {

        this.registrationService = registrationService;
        this.activationEmailService = activationEmailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {
            RegistrationRequest registrationRequest =
                    new RegistrationRequest();

            registrationRequest.setFullName(request.getFullName());
            registrationRequest.setEmail(request.getEmail());
            registrationRequest.setMobile(request.getMobile());

            RegistrationResult registrationResult =
                    registrationService.register(registrationRequest);

            // Send activation email
            activationEmailService.sendActivationEmail(
                    registrationResult.getRegistrationRequest().getEmail(),
                    registrationResult.getRegistrationRequest().getFullName(),
                    registrationResult.getActivationToken()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message",
                            "Registration successful. Please check your email to activate your account.",
                            "status",
                            registrationResult.getRegistrationRequest()
                                    .getStatus().name()
                    ));

        } catch (IllegalArgumentException exception) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    public static class RegisterRequest {

        private String fullName;
        private String email;
        private String mobile;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }
}
