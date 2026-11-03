package com.naveen.employeemanagement.controller;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.entity.RegistrationRequest;
import com.naveen.employeemanagement.dto.PendingRegistrationResponse;
import com.naveen.employeemanagement.service.RegistrationService;
import jakarta.servlet.Registration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final RegistrationService registrationService;

    public AdminController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/pending-registrations")
    public ResponseEntity<List<PendingRegistrationResponse>> getPendingRegistrations() {

        return ResponseEntity.ok(registrationService.getPendingRegistrations());
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Map<String, Object>> approve(
            @PathVariable Long id, @RequestBody ApprovalRequest request) {

        Employee employee = registrationService.approveRegistration(
                id, request.getEmployeeCode(),
                request.getDepartment(),
                request.getDesignation(),
                request.getSalary(),
                request.getJoiningDate());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration approved successfully");
        response.put("employeeCode", employee.getEmployeeCode());
        response.put("email", employee.getEmail());
        response.put("status", employee.getEmployeeStatus());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectRegistrastion(@PathVariable Long id) {

        RegistrationRequest request = registrationService.rejectRegistration(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration rejected successfully");
        response.put("status", request.getStatus().name());

        return ResponseEntity.ok(response);
    }

    public static class ApprovalRequest {

        private String employeeCode;
        private String department;
        private String designation;
        private double salary;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDateTime joiningDate;

        public String getEmployeeCode() {
            return employeeCode;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public LocalDateTime getJoiningDate() {
            return joiningDate;
        }

        public void setJoiningDate(LocalDateTime joiningDate) {
            this.joiningDate = joiningDate;
        }

        public double getSalary() {
            return salary;
        }

        public void setSalary(double salary) {
            this.salary = salary;
        }

    }


}
