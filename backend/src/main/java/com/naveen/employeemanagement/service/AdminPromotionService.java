package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.entity.AdminPromotionRequest;
import com.naveen.employeemanagement.entity.AdminPromotionRequest.Status;
import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.repository.AdminPromotionRequestRepository;
import com.naveen.employeemanagement.repository.EmployeeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminPromotionService {

    private final AdminPromotionRequestRepository requestRepository;
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

    public AdminPromotionService(
            AdminPromotionRequestRepository requestRepository,
            EmployeeRepository employeeRepository,
            EntityManager entityManager) {

        this.requestRepository = requestRepository;
        this.employeeRepository = employeeRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public AdminPromotionRequest requestPromotion(
            Long employeeId,
            String reason) {

        Employee requester = requireAdmin();

        if (employeeId == null) {
            throw new IllegalArgumentException(
                    "Employee ID is required."
            );
        }

        Employee target = lockEmployee(employeeId);
        requireEligibleEmployee(target);

        if (requestRepository.existsByTargetEmployeeIdAndStatus(
                employeeId, Status.PENDING)) {

            throw new IllegalArgumentException(
                    "A pending promotion request already exists."
            );
        }

        AdminPromotionRequest request = new AdminPromotionRequest(
                target.getId(),
                requester.getId(),
                reason
        );

        // The owner's own request includes final approval.
        if ("SUPER_ADMIN".equals(requester.getRole())) {
            target.setRole("ADMIN");
            employeeRepository.save(target);

            request.recordDecision(
                    Status.APPROVED,
                    requester.getId(),
                    "Direct approval by SUPER_ADMIN."
            );
        }

        return requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<AdminPromotionRequest> getPendingRequests() {
        requireSuperAdmin();

        return requestRepository
                .findByStatusOrderByCreatedAtDesc(Status.PENDING);
    }

    @Transactional(readOnly = true)
    public List<AdminPromotionRequest> getMyRequests() {
        Employee requester = requireAdmin();

        return requestRepository
                .findByRequestedByIdOrderByCreatedAtDesc(
                        requester.getId()
                );
    }

    @Transactional
    public AdminPromotionRequest approve(
            Long requestId,
            String comment) {

        Employee reviewer = requireSuperAdmin();

        AdminPromotionRequest request =
                lockPendingRequest(requestId);

        Employee target =
                lockEmployee(request.getTargetEmployeeId());

        requireEligibleEmployee(target);

        request.recordDecision(
                Status.APPROVED,
                reviewer.getId(),
                comment
        );

        target.setRole("ADMIN");
        employeeRepository.save(target);

        return requestRepository.save(request);
    }

    @Transactional
    public AdminPromotionRequest reject(
            Long requestId,
            String comment) {

        Employee reviewer = requireSuperAdmin();

        AdminPromotionRequest request =
                lockPendingRequest(requestId);

        request.recordDecision(
                Status.REJECTED,
                reviewer.getId(),
                comment
        );

        return requestRepository.save(request);
    }

    private Employee requireAdmin() {
        Employee actor = currentEmployee();

        if (!"ADMIN".equals(actor.getRole())
                && !"SUPER_ADMIN".equals(actor.getRole())) {

            throw new AccessDeniedException(
                    "Admin access is required."
            );
        }

        return actor;
    }

    private Employee requireSuperAdmin() {
        Employee actor = currentEmployee();

        if (!"SUPER_ADMIN".equals(actor.getRole())) {
            throw new AccessDeniedException(
                    "Only SUPER_ADMIN can review promotion requests."
            );
        }

        return actor;
    }

    private Employee currentEmployee() {
        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "Authentication is required."
            );
        }

        Employee actor = employeeRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Authenticated account was not found."
                        )
                );

        if (!"ACTIVE".equals(actor.getEmploymentStatus())) {
            throw new AccessDeniedException(
                    "An active account is required."
            );
        }

        return actor;
    }

    private Employee lockEmployee(Long employeeId) {
        Employee employee = entityManager.find(
                Employee.class,
                employeeId,
                LockModeType.PESSIMISTIC_WRITE
        );

        if (employee == null) {
            throw new IllegalArgumentException(
                    "Employee was not found."
            );
        }

        return employee;
    }

    private void requireEligibleEmployee(Employee employee) {
        if (!"EMPLOYEE".equals(employee.getRole())) {
            throw new IllegalArgumentException(
                    "Only an EMPLOYEE can be promoted to ADMIN."
            );
        }

        if (!"ACTIVE".equals(employee.getEmploymentStatus())) {
            throw new IllegalArgumentException(
                    "Only an active employee can be promoted."
            );
        }
    }

    private AdminPromotionRequest lockPendingRequest(Long requestId) {
        if (requestId == null) {
            throw new IllegalArgumentException(
                    "Request ID is required."
            );
        }

        AdminPromotionRequest request = requestRepository
                .findByIdForUpdate(requestId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Promotion request was not found."
                        )
                );

        if (request.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException(
                    "This request has already been reviewed."
            );
        }

        return request;
    }
}