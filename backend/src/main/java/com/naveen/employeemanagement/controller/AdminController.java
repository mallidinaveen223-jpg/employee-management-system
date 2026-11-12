package com.naveen.employeemanagement.controller;

import com.naveen.employeemanagement.dto.EmployeeResponse;
import com.naveen.employeemanagement.dto.UpdateEmployeeRequest;
import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.entity.RegistrationRequest;
import com.naveen.employeemanagement.dto.PendingRegistrationResponse;
import com.naveen.employeemanagement.service.DashboardStatisticsService;
import com.naveen.employeemanagement.service.EmployeeService;
import com.naveen.employeemanagement.service.RegistrationService;
import com.naveen.employeemanagement.dto.EmployeeRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import  jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class AdminController {

    private final RegistrationService registrationService;
    private final EmployeeService employeeService;
    private final DashboardStatisticsService dashboardStatisticsService;

    public AdminController(RegistrationService registrationService,
                           EmployeeService employeeService, DashboardStatisticsService dashboardStatisticsService) {
        this.registrationService = registrationService;
        this.employeeService = employeeService;
        this.dashboardStatisticsService = dashboardStatisticsService;
    }

    @GetMapping("/pending-registrations")
    public ResponseEntity<List<PendingRegistrationResponse>> getPendingRegistrations() {

        return ResponseEntity.ok(registrationService.getPendingRegistrations());
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {

        List<EmployeeResponse> employees = employeeService.getAllEmployees()
                .stream()
                .map(EmployeeResponse::from)
                .toList();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {

        return ResponseEntity.ok(
                dashboardStatisticsService.getDashbordStatistics());
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request){

        Employee savedEmployee = employeeService.createEmployee(request);

        return ResponseEntity.ok(
                EmployeeResponse.from(savedEmployee));
    }
    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request){

        Employee updatedEmployee = employeeService.updateEmployee(id, request);

        return  ResponseEntity.ok(EmployeeResponse.from(updatedEmployee));
    }
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(
            @PathVariable Long id){

        employeeService.deleteEmployee(id);

        return ResponseEntity.ok(Map.of("message", "Employee deleted successfully"));
    }
    @PutMapping("/approve/{id}")
    public ResponseEntity<Map<String, Object>> approve(
            @PathVariable Long id, @RequestBody ApprovalRequest request) {

        Employee employee = registrationService.approveRegistration(
                id,
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

        private String department;
        private String designation;
        private double salary;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDateTime joiningDate;
        
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
