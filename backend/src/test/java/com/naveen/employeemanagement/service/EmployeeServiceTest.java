package com.naveen.employeemanagement.service;


import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.exception.EmployeeNotFoundException;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {


    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void saveEmployee_shouldReturnSavedEmployee() {

        Employee employee = new Employee();
        employee.setName("Naveen Kumar");
        employee.setEmail("naveen@gmail.com");
        employee.setDepartment("IT");
        employee.setSalary(50000);

        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee savedEmployee = employeeService.saveEmployee(employee);

        assertEquals("Naveen Kumar", savedEmployee.getName());
        assertEquals("naveen@gmail.com", savedEmployee.getEmail());

        verify(employeeRepository).save(employee);
    }
    @Test
    void getAllEmployees_shouldReturnListOfEmployees() {

        Employee employee1 = new Employee();
        employee1.setName("Naveen Kumar");
        employee1.setEmail("naveen@gmail.clm");
        employee1.setDepartment("IT");
        employee1.setSalary(50000);

        Employee employee2 = new Employee();
        employee2.setName("John Doe");
        employee2.setEmail("John@gmail.com");
        employee2.setDepartment("HR");
        employee2.setSalary(60000);

        when(employeeRepository.findAll())
                .thenReturn(List.of(employee1, employee2));

        List<Employee> employees = employeeService.getAllEmployees();

        assertEquals(2, employees.size());
        assertEquals("Naveen Kumar", employees.get(0).getName());
        assertEquals("John Doe", employees.get(1).getName());

        verify(employeeRepository).findAll();
    }
    @Test
    void updateEmployee_shouldReturnUpdatedEmployee() {

        Employee existingEmployee = new Employee();
        existingEmployee.setId(1L);
        existingEmployee.setName("Naveen Kumar");
        existingEmployee.setEmail("naveen@gmail.com");
        existingEmployee.setDepartment("IT");
        existingEmployee.setSalary(50000);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Naveen K");
        updatedEmployee.setEmail("naveen.k@gmail.com");
        updatedEmployee.setDepartment("Technology");
        updatedEmployee.setSalary(60000);

        when(employeeRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existingEmployee));

        when(employeeRepository.save(existingEmployee))
                .thenReturn(existingEmployee);

        Employee result = employeeService.updateEmployee(1L, updatedEmployee);

        assertEquals("Technology", result.getDepartment());
        assertEquals(60000, result.getSalary());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(existingEmployee);
    }
    @Test
    void deleteEmployee_shouldDeleteEmployee(){

        Employee employee = new Employee();
        employee.setName("Naveen Kumar");
        employee.setEmail("naveen@gmail.com");
        employee.setDepartment("IT");
        employee.setSalary(50000);

        when(employeeRepository.findById(1L))
                .thenReturn(java.util.Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).delete(employee);

    }
    @Test
    void updateEmployee_shouldThrowExceptionWhenEmployeeNotFound() {

        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Naveen K");
        updatedEmployee.setEmail("naveen.k@gmail.com");
        updatedEmployee.setDepartment("Technology");
        updatedEmployee.setSalary(60000);

        when(employeeRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(999L, updatedEmployee)
        );

        verify(employeeRepository).findById(999L);
    }
    @Test
    void getAllEmployees_shouldReturnEmptyListWhenNoEmployees() {

        when(employeeRepository.findAll())
                .thenReturn(java.util.Collections.emptyList());

        List<Employee> employees = employeeService.getAllEmployees();

        assertEquals(0, employees.size());

        verify(employeeRepository).findAll();
    }
}