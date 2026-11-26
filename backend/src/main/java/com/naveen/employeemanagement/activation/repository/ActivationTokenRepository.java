package com.naveen.employeemanagement.activation.repository;

import com.naveen.employeemanagement.activation.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    Optional<ActivationToken> findByTokenHash(String tokenHash);

    void deleteByEmail(String email);

    List<ActivationToken> email(String email);
}
