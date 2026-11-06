package com.naveen.employeemanagement.controller;

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

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        try {
            RegistrationRequest registrationRequest = new RegistrationRequest();

            registrationRequest.setFullName(request.getFullName());
            registrationRequest.setEmail(request.getEmail());
            registrationRequest.setMobile(request.getMobile());

            // Raw password is temporarily passed here.
            // RegistrationService will encrypt it using BCrypt.
            registrationRequest.setPasswordHash(request.getPassword());

            RegistrationRequest savedRequest =
                    registrationService.register(registrationRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Registration successful. Admin approval is required.",
                            "status", savedRequest.getStatus().name()
                    ));

        } catch (IllegalArgumentException exception) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", exception.getMessage()));
        }
    }

    public static class RegisterRequest {

        private String fullName;
        private String email;
        private String mobile;
        private String password;

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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}