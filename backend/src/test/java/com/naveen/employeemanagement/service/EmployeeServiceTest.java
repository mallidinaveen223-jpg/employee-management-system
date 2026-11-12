
package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.dto.EmployeeRequest;
import com.naveen.employeemanagement.dto.UpdateEmployeeRequest;
import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.exception.EmployeeNotFoundException;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    // 1. Save Employee
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

        verify(employeeRepository, times(1)).save(employee);
    }

    // 2. Create Employee Successfully
    @Test
    void createEmployee_shouldReturnSavedEmployee() {

        Employee employee = new Employee();
        employee.setName("Naveen Kumar");
        employee.setEmail("naveen@gmail.com");
        employee.setEmployeeCode("EMP001");
        employee.setPasswordHash("plainPassword");

        when(employeeRepository.findByEmail("naveen@gmail.com"))
                .thenReturn(Optional.empty());

        when(employeeRepository.existsByEmployeeCode("EMP001"))
                .thenReturn(false);

        when(passwordEncoder.encode("plainPassword"))
                .thenReturn("encodedPassword");

        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee savedEmployee = employeeService.createEmployee(employee);

        assertEquals("Naveen Kumar", savedEmployee.getName());
        assertEquals("encodedPassword", savedEmployee.getPasswordHash());
        assertEquals("EMPLOYEE", savedEmployee.getRole());
        assertEquals("ACTIVE", savedEmployee.getEmploymentStatus());

        verify(passwordEncoder).encode("plainPassword");
        verify(employeeRepository).save(employee);
    }


    @Test
    void createEmployee_shouldAllowNullEmployeeCode() {

        Employee employee = new Employee();
        employee.setName("Naveen Kumar");
        employee.setEmail("naveen@gmail.com");
        employee.setEmployeeCode(null);
        employee.setPasswordHash("plainPassword");

        when(employeeRepository.findByEmail("naveen@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("plainPassword"))
                .thenReturn("encodedPassword");

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        Employee result = employeeService.createEmployee(employee);

        assertEquals("Naveen Kumar", result.getName());
        assertEquals("encodedPassword", result.getPasswordHash());

        verify(employeeRepository, never())
                .existsByEmployeeCode(anyString());

        verify(employeeRepository).save(employee);
    }


    // 3. Create Employee - Duplicate Email
    @Test
    void createEmployee_shouldThrowExceptionWhenEmailExists() {

        Employee employee = new Employee();
        employee.setEmail("naveen@gmail.com");

        when(employeeRepository.findByEmail("naveen@gmail.com"))
                .thenReturn(Optional.of(employee));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(employee)
        );

        assertEquals("Email already exists", exception.getMessage());

        verify(employeeRepository, never()).save(any(Employee.class));
        verifyNoInteractions(passwordEncoder);
    }

    // 4. Create Employee - Duplicate Employee Code
    @Test
    void createEmployee_shouldThrowExceptionWhenEmployeeCodeExists() {

        Employee employee = new Employee();
        employee.setEmail("new@gmail.com");
        employee.setEmployeeCode("EMP001");

        when(employeeRepository.findByEmail("new@gmail.com"))
                .thenReturn(Optional.empty());

        when(employeeRepository.existsByEmployeeCode("EMP001"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(employee)
        );

        assertEquals("Employee code already exists", exception.getMessage());

        verify(employeeRepository, never()).save(any(Employee.class));
        verifyNoInteractions(passwordEncoder);
    }

    // 5. Get All Employees
    @Test
    void getAllEmployees_shouldReturnListOfEmployees() {

        Employee employee1 = new Employee();
        employee1.setName("Naveen Kumar");
        employee1.setEmail("naveen@gmail.com");
        employee1.setDepartment("IT");
        employee1.setSalary(50000);

        Employee employee2 = new Employee();
        employee2.setName("John Doe");
        employee2.setEmail("john@gmail.com");
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

    // 6. Get All Employees - Empty List
    @Test
    void getAllEmployees_shouldReturnEmptyListWhenNoEmployees() {

        when(employeeRepository.findAll())
                .thenReturn(List.of());

        List<Employee> employees = employeeService.getAllEmployees();

        assertTrue(employees.isEmpty());

        verify(employeeRepository).findAll();
    }

    // 7. Get Employee By Email
    @Test
    void getEmployeeByEmail_shouldReturnEmployee() {

        Employee employee = new Employee();
        employee.setName("Naveen Kumar");
        employee.setEmail("naveen@gmail.com");

        when(employeeRepository.findByEmail("naveen@gmail.com"))
                .thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeByEmail("naveen@gmail.com");

        assertEquals("Naveen Kumar", result.getName());
        assertEquals("naveen@gmail.com", result.getEmail());

        verify(employeeRepository).findByEmail("naveen@gmail.com");
    }

    // 8. Get Employee By Email - Not Found
    @Test
    void getEmployeeByEmail_shouldThrowExceptionWhenNotFound() {

        when(employeeRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeByEmail("unknown@gmail.com")
        );

        assertEquals(
                "Employee not found with email: unknown@gmail.com",
                exception.getMessage()
        );

        verify(employeeRepository).findByEmail("unknown@gmail.com");
    }

    // 9. Get Employee By ID
    @Test
    void getEmployeeById_shouldReturnEmployee() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Naveen Kumar");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeByI(1L);

        assertEquals(1L, result.getId());
        assertEquals("Naveen Kumar", result.getName());

        verify(employeeRepository).findById(1L);
    }

    // 10. Get Employee By ID - Not Found
    @Test
    void getEmployeeById_shouldThrowExceptionWhenNotFound() {

        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeByI(999L)
        );

        assertEquals(
                "Employee not found with id: 999",
                exception.getMessage()
        );

        verify(employeeRepository).findById(999L);
    }

    // 11. Update Employee
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
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository.save(existingEmployee))
                .thenReturn(existingEmployee);

        Employee result = employeeService.updateEmployee(1L, updatedEmployee);

        assertEquals("Naveen K", result.getName());
        assertEquals("naveen.k@gmail.com", result.getEmail());
        assertEquals("Technology", result.getDepartment());
        assertEquals(60000, result.getSalary());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(existingEmployee);
    }

    // 12. Update Employee - Not Found
    @Test
    void updateEmployee_shouldThrowExceptionWhenEmployeeNotFound() {

        Employee updatedEmployee = new Employee();

        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(999L, updatedEmployee)
        );

        assertEquals(
                "Employee not found with id: 999",
                exception.getMessage()
        );

        verify(employeeRepository).findById(999L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    // 13. Delete Employee
    @Test
    void deleteEmployee_shouldDeleteEmployee() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Naveen Kumar");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).delete(employee);
    }

    // 14. Delete Employee - Not Found
    @Test
    void deleteEmployee_shouldThrowExceptionWhenEmployeeNotFound() {

        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(999L)
        );

        assertEquals(
                "Employee not found with id: 999",
                exception.getMessage()
        );

        verify(employeeRepository).findById(999L);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
    @Test
    void createEmployee_withDTO_shouldReturnSavedEmployee() {

        EmployeeRequest request = new EmployeeRequest();
        request.setName("Naveen Kumar");
        request.setEmail("naveen@gmail.com");
        request.setPassword("password123");
        request.setDepartment("IT");
        request.setSalary(50000.0);
        request.setMobile("9876543210");
        request.setDesignation("Java Developer");

        when(employeeRepository.findByEmail("naveen@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Employee result = employeeService.createEmployee(request);

        assertEquals("Naveen Kumar", result.getName());
        assertEquals("naveen@gmail.com", result.getEmail());
        assertEquals("encodedPassword", result.getPasswordHash());
        assertEquals("IT", result.getDepartment());
        assertEquals(50000.0, result.getSalary());
        assertEquals("EMPLOYEE", result.getRole());
        assertEquals("ACTIVE", result.getEmploymentStatus());

        verify(passwordEncoder).encode("password123");
        verify(employeeRepository).save(any(Employee.class));
    }
    @Test
    void updateEmployee_withDTO_shouldReturnUpdatedEmployee() {

        Employee existingEmployee = new Employee();
        existingEmployee.setId(1L);
        existingEmployee.setName("Naveen");
        existingEmployee.setEmail("old@gmail.com");

        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setName("Naveen Kumar");
        request.setEmail("naveen@gmail.com");
        request.setDepartment("IT");
        request.setDesignation("Senior Java Developer");
        request.setSalary(60000.0);
        request.setMobile("9876543210");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository.save(existingEmployee))
                .thenReturn(existingEmployee);

        Employee result = employeeService.updateEmployee(1L, request);

        assertEquals("Naveen Kumar", result.getName());
        assertEquals("naveen@gmail.com", result.getEmail());
        assertEquals("IT", result.getDepartment());
        assertEquals("Senior Java Developer", result.getDesignation());
        assertEquals(60000.0, result.getSalary());
        assertEquals("9876543210", result.getMobile());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(existingEmployee);
    }
}