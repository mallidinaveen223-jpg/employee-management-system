
package com.naveen.employeemanagement.controller;

import com.naveen.employeemanagement.dto.EmployeeRequest;
import com.naveen.employeemanagement.dto.EmployeeResponse;
import com.naveen.employeemanagement.dto.UpdateEmployeeRequest;
import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    void createEmployee_ShouldReturnCreatedEmployee() {

        EmployeeRequest request = new EmployeeRequest();
        request.setName("Naveen Kumar");
        request.setEmail("naveen.kumar@example.com");
        request.setPassword("Password@123");
        request.setDepartment("IT");
        request.setSalary(50000.0);

        Employee employee = new Employee();
        employee.setName("Naveen Kumar");
        employee.setEmail("naveen.kumar@example.com");
        employee.setDepartment("IT");
        employee.setSalary(50000.0);

        when(employeeService.createEmployee(request)).thenReturn(employee);

        ResponseEntity<EmployeeResponse> response =
                employeeController.createEmployee(request);

        assertEquals("Naveen Kumar", response.getBody().name());
        assertEquals("naveen.kumar@example.com", response.getBody().email());

        verify(employeeService).createEmployee(request);
    }

    @Test
    void getAllEmployees_ShouldReturnList() {

        Employee employee1 = new Employee();
        employee1.setName("Naveen Kumar");
        employee1.setEmail("naveen.kumar@example.com");
        employee1.setDepartment("IT");
        employee1.setSalary(50000.0);

        Employee employee2 = new Employee();
        employee2.setName("John Doe");
        employee2.setEmail("john.doe@example.com");
        employee2.setDepartment("HR");
        employee2.setSalary(60000.0);

        when(employeeService.getAllEmployees())
                .thenReturn(Arrays.asList(employee1, employee2));

        List<Employee> result = employeeController.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals("Naveen Kumar", result.get(0).getName());
        assertEquals("John Doe", result.get(1).getName());

        verify(employeeService).getAllEmployees();
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() {

        Long employeeId = 1L;

        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setName("Naveen Kumar");
        request.setEmail("naveen.kumar.updated@example.com");
        request.setDepartment("IT");
        request.setSalary(55000.0);

        Employee employee = new Employee();
        employee.setName("Naveen Kumar");
        employee.setEmail("naveen.kumar.updated@example.com");
        employee.setDepartment("IT");
        employee.setSalary(55000.0);

        when(employeeService.updateEmployee(employeeId, request))
                .thenReturn(employee);

        ResponseEntity<EmployeeResponse> response =
                employeeController.updateEmployee(employeeId, request);

        assertEquals("Naveen Kumar", response.getBody().name());
        assertEquals("naveen.kumar.updated@example.com",
                response.getBody().email());

        verify(employeeService).updateEmployee(employeeId, request);
    }

    @Test
    void deleteEmployee_ShouldInvokeServiceDeleteMethod() {

        Long employeeId = 1L;

        employeeController.deleteEmployee(employeeId);

        verify(employeeService).deleteEmployee(employeeId);
    }
}
