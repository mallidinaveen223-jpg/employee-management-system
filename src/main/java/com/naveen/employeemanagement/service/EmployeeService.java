package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.naveen.employeemanagement.exception.EmployeeNotFoundException;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder) {

        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    public Employee createEmployee(Employee employee) {

        if(employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        if(employee.getEmployeeCode() != null &&
                employeeRepository.existsByEmployeeCode((employee.getEmployeeCode()))){
            throw new IllegalArgumentException("Employee code already exists");
        }
        employee.setPasswordHash(
                passwordEncoder.encode(employee.getPasswordHash()));

        employee.setRole("EMPLOYEE");
        employee.setEmploymentStatus("ACTIVE");

        return  employeeRepository.save(employee);
    }
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    public Employee getEmployeeByEmail(String email){

        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with email: "+ email));
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee){

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with id: " + id));

        existingEmployee.setName(updatedEmployee.getName());
        existingEmployee.setEmail(updatedEmployee.getEmail());
        existingEmployee.setMobile(updatedEmployee.getMobile());
        existingEmployee.setDepartment(updatedEmployee.getDepartment());
        existingEmployee.setDesignation(updatedEmployee.getDesignation());
        existingEmployee.setSalary(updatedEmployee.getSalary());
        existingEmployee.setJoiningDate(updatedEmployee.getJoiningDate());
        existingEmployee.setEmploymentStatus(updatedEmployee.getEmploymentStatus());

        return employeeRepository.save(existingEmployee);
    }
    public void deleteEmployee(Long id){

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with id: " + id));

        employeeRepository.delete(existingEmployee);
    }
}
