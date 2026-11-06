package com.naveen.employeemanagement.dto;

import com.naveen.employeemanagement.entity.Employee;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        String department,
        Double salary,
        String employeeCode,
        String mobile,
        String designation,
        String role,
        LocalDateTime joiningDate,
        String employeeStatus
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
            employee.getId(),
            employee.getName(),
            employee.getEmail(),
            employee.getDepartment(),
            employee.getSalary(),
            employee.getEmployeeCode(),
            employee.getMobile(),
            employee.getDesignation(),
            employee.getRole(),
            employee.getJoiningDate(),
            employee.getEmploymentStatus()
    );
}
}
