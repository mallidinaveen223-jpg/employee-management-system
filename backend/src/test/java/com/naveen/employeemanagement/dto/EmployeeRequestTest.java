package com.naveen.employeemanagement.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeRequestTest {

    @Test
    void employeeRequest_shouldSetAndGetAllFields() {

        EmployeeRequest request = new EmployeeRequest();

        request.setName("Naveen Kumar");
        request.setEmail("naveen@gmail.com");
        request.setPassword("password123");
        request.setDepartment("IT");
        request.setSalary(50000.0);
        request.setMobile("9876543210");
        request.setDesignation("Java Developer");

        assertEquals("Naveen Kumar", request.getName());
        assertEquals("naveen@gmail.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("IT", request.getDepartment());
        assertEquals(50000.0, request.getSalary());
        assertEquals("9876543210", request.getMobile());
        assertEquals("Java Developer", request.getDesignation());
    }
}