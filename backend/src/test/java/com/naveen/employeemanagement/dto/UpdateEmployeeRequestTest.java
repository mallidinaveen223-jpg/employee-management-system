package com.naveen.employeemanagement.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateEmployeeRequestTest {

    @Test
    void updateEmployeeRequest_shouldSetAndGetAllFields() {

        UpdateEmployeeRequest request = new UpdateEmployeeRequest();

        request.setName("Naveen Kumar");
        request.setEmail("naveen@gmail.com");
        request.setDepartment("IT");
        request.setSalary(60000.0);
        request.setMobile("9876543210");
        request.setDesignation("Senior Java Developer");

        assertEquals("Naveen Kumar", request.getName());
        assertEquals("naveen@gmail.com", request.getEmail());
        assertEquals("IT", request.getDepartment());
        assertEquals(60000.0, request.getSalary());
        assertEquals("9876543210", request.getMobile());
        assertEquals("Senior Java Developer", request.getDesignation());
    }

}