
package com.naveen.employeemanagement.repository;

import com.naveen.employeemanagement.entity.EmployeeCodeRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeCodeRegistryRepository
        extends JpaRepository<EmployeeCodeRegistry, String> {

}
