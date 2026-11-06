package com.naveen.employeemanagement.dto;

import com.naveen.employeemanagement.entity.Employee;

public record EmployeeDirectoryResponse(

    Long id,
    String name,
    String email,
    String mobile,
    String department,
    String designation,
    String role
){
    public static EmployeeDirectoryResponse from(Employee employee){
        return new EmployeeDirectoryResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getMobile(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getRole()
        );

    }
}
