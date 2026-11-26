
package com.naveen.employeemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_code_registry")
public class EmployeeCodeRegistry {

    @Id
    @Column(name = "employee_code", length = 5, nullable = false)
    private String employeeCode;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public EmployeeCodeRegistry() {
    }

    public EmployeeCodeRegistry(String employeeCode) {
        this.employeeCode = employeeCode;
        this.generatedAt = LocalDateTime.now();
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}
