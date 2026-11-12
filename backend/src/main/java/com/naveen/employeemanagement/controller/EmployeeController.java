
package com.naveen.employeemanagement.controller;

import com.naveen.employeemanagement.dto.*;
import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @RequestBody @Valid EmployeeRequest request) {

        Employee savedEmployee = employeeService.createEmployee(request);

        return ResponseEntity.ok(EmployeeResponse.from(savedEmployee));
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeeResponse> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        Employee employee =
                employeeService.getEmployeeByEmail(email);

        return ResponseEntity.ok(EmployeeResponse.from(employee));
    }

    @GetMapping("/directory")
    public ResponseEntity<List<EmployeeDirectoryResponse>> getEmployeeDirectory() {

        List<EmployeeDirectoryResponse> employees =
                employeeService.getAllEmployees()
                        .stream()
                        .filter(employee ->
                                "ACTIVE".equalsIgnoreCase(
                                        employee.getEmployeeStatus()))
                        .map(EmployeeDirectoryResponse::from)
                        .toList();

        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody @Valid UpdateEmployeeRequest request) {

        Employee updatedEmployee = employeeService.updateEmployee(id, request);

        return ResponseEntity.ok(EmployeeResponse.from(updatedEmployee));
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}
