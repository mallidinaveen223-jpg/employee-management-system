package com.naveen.employeemanagement.repository;

import com.naveen.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmail(String email);

    Boolean existsByRole(String role);

    long countByEmploymentStatus(String employmentStatus);

    long countByJoiningDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByDepartment(String department);

    @Query("""
SELECT e.department, COUNT(e.id)
FROM Employee e
GROUP BY e.department
ORDER BY e.department
""")
    List<Object[]> getDepartmentWiseEmployeeCount();

}