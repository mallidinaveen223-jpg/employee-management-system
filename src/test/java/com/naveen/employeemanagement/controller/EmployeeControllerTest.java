package com.naveen.employeemanagement.controller;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

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
    void creatEmployee_ShouldReturnCrestedEmployee(){

        Employee employee = new Employee();
        employee.setName("Naveen Kumar");
        employee.setEmail("naveen.kumar@example.com");
        employee.setDepartment("IT");
        employee.setSalary(50000);

        when(employeeService.saveEmployee(employee))
                .thenReturn(employee);

        Employee result = employeeController.createEmployee(employee);

        assertEquals("Naveen Kumar", result.getName());
        assertEquals("naveen.kumar@example.com", result.getEmail());

        verify(employeeService).saveEmployee(employee);
    }
    @Test
    void getAllEmployees_ShouldReturnList(){

        Employee employee1 = new Employee();
        employee1.setName("Naveen Kumar");
        employee1.setEmail("naveen.kumar@example.com");
        employee1.setDepartment("IT");
        employee1.setSalary(50000);

        Employee employee2 = new Employee();
        employee2.setName("John Doe");
        employee2.setEmail("john.doe@example.com");
        employee2.setDepartment("HR");
        employee2.setSalary(60000);

        when(employeeService.getAllEmployees())
                .thenReturn(Arrays.asList(employee1, employee2));

        java.util.List<Employee> result = employeeController.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals("Naveen Kumar", result.get(0).getName());
        assertEquals("John Doe", result.get(1).getName());

        verify(employeeService).getAllEmployees();
    }
    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee(){

        Long employeeId = 1L;

        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Naveen Kumar");
        updatedEmployee.setEmail("naveen.kumar.updated@example.com");
        updatedEmployee.setDepartment("IT");
        updatedEmployee.setSalary(55000);

        when(employeeService.updateEmployee(employeeId, updatedEmployee))
                .thenReturn(updatedEmployee);

        Employee result = employeeController.updateEmployee(employeeId, updatedEmployee);

        assertEquals("Naveen Kumar", result.getName());
        assertEquals("naveen.kumar.updated@example.com", result.getEmail());

        verify(employeeService).updateEmployee(employeeId, updatedEmployee);
    }
    @Test
    void deleteEmployee_ShouldInvokeServiceDeleteMethod(){

        Long employeeId = 1L;

        employeeController.deleteEmployee(employeeId);

        verify(employeeService).deleteEmployee(employeeId);
    }
}