package com.naveen.employeemanagement.repository;

import com.naveen.employeemanagement.entity.AdminPromotionRequest;
import com.naveen.employeemanagement.entity.AdminPromotionRequest.Status;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminPromotionRequestRepository
        extends JpaRepository<AdminPromotionRequest, Long> {

    List<AdminPromotionRequest> findByStatusOrderByCreatedAtDesc(
            Status status
    );

    List<AdminPromotionRequest> findByRequestedByIdOrderByCreatedAtDesc(
            Long requestedById
    );

    boolean existsByTargetEmployeeIdAndStatus(
            Long targetEmployeeId,
            Status status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT request
            FROM AdminPromotionRequest request
            WHERE request.id = :id
            """)
    Optional<AdminPromotionRequest> findByIdForUpdate(
            @Param("id") Long id
    );
}