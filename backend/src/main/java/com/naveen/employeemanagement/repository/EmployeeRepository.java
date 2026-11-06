package com.naveen.employeemanagement.repository;

import com.naveen.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmail(String email);

    Boolean existsByRole(String role);

}