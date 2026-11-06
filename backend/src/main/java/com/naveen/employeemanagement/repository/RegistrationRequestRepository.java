package com.naveen.employeemanagement.repository;

import com.naveen.employeemanagement.entity.RegistrationRequest;
import com.naveen.employeemanagement.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {

    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    Optional<RegistrationRequest> findByEmail(String email);

    List<RegistrationRequest> findByStatus(RegistrationStatus status);
}
