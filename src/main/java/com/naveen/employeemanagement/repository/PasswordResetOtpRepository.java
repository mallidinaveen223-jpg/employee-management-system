package com.naveen.employeemanagement.repository;

import com.naveen.employeemanagement.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmail(String email);

    void deleteByEmail(String email);

    List<PasswordResetOtp> email(String email);
}