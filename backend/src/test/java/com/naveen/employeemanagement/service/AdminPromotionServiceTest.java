
package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.entity.AdminPromotionRequest;
import com.naveen.employeemanagement.entity.AdminPromotionRequest.Status;
import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.repository.AdminPromotionRequestRepository;
import com.naveen.employeemanagement.repository.EmployeeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPromotionServiceTest {

    @Mock
    private AdminPromotionRequestRepository requestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Authentication authentication;

    @Mock
    private Employee requester;

    @Mock
    private Employee target;

    @InjectMocks
    private AdminPromotionService promotionService;

    @BeforeEach
    void setUp() {

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin@test.com");

        when(employeeRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(requester));

        when(requester.getEmploymentStatus()).thenReturn("ACTIVE");
    }

    private void stubTargetEmployee() {

        when(target.getRole()).thenReturn("EMPLOYEE");
        when(target.getEmploymentStatus()).thenReturn("ACTIVE");

        when(entityManager.find(
                eq(Employee.class),
                eq(20L),
                eq(LockModeType.PESSIMISTIC_WRITE)
        )).thenReturn(target);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // 1. ADMIN creates a pending promotion request
    @Test
    void requestPromotionByAdminShouldCreatePendingRequest() {

        stubTargetEmployee();

        when(requester.getRole()).thenReturn("ADMIN");
        when(requester.getId()).thenReturn(10L);
        when(target.getId()).thenReturn(20L);

        when(requestRepository.existsByTargetEmployeeIdAndStatus(
                20L, Status.PENDING
        )).thenReturn(false);

        when(requestRepository.save(any(AdminPromotionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminPromotionRequest result =
                promotionService.requestPromotion(20L, "Good performance");

        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
        assertEquals(20L, result.getTargetEmployeeId());
        assertEquals(10L, result.getRequestedById());

        verify(requestRepository).save(any(AdminPromotionRequest.class));
        verify(employeeRepository, never()).save(target);
    }

    // 2. SUPER_ADMIN directly promotes an employee
    @Test
    void requestPromotionBySuperAdminShouldApproveImmediately() {

        stubTargetEmployee();

        when(requester.getRole()).thenReturn("SUPER_ADMIN");
        when(requester.getId()).thenReturn(10L);
        when(target.getId()).thenReturn(20L);

        when(requestRepository.existsByTargetEmployeeIdAndStatus(
                20L, Status.PENDING
        )).thenReturn(false);

        when(requestRepository.save(any(AdminPromotionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminPromotionRequest result =
                promotionService.requestPromotion(20L, "Direct promotion");

        assertEquals(Status.APPROVED, result.getStatus());
        assertEquals(10L, result.getReviewedById());

        verify(target).setRole("ADMIN");
        verify(employeeRepository).save(target);
        verify(requestRepository).save(any(AdminPromotionRequest.class));
    }

    // 3. Reject duplicate pending promotion request
    @Test
    void requestPromotionShouldRejectDuplicatePendingRequest() {

        stubTargetEmployee();

        when(requester.getRole()).thenReturn("ADMIN");

        when(requestRepository.existsByTargetEmployeeIdAndStatus(
                20L, Status.PENDING
        )).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.requestPromotion(
                        20L, "Promotion request"
                )
        );

        verify(requestRepository, never())
                .save(any(AdminPromotionRequest.class));
    }

    // 4. SUPER_ADMIN approves a pending request
    @Test
    void approveShouldPromoteEmployeeToAdmin() {

        stubTargetEmployee();

        when(requester.getRole()).thenReturn("SUPER_ADMIN");
        when(requester.getId()).thenReturn(10L);

        AdminPromotionRequest request =
                new AdminPromotionRequest(
                        20L, 10L, "Good performance"
                );

        when(requestRepository.findByIdForUpdate(100L))
                .thenReturn(Optional.of(request));

        when(requestRepository.save(any(AdminPromotionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminPromotionRequest result =
                promotionService.approve(100L, "Approved");

        assertEquals(Status.APPROVED, result.getStatus());
        assertEquals(10L, result.getReviewedById());

        verify(target).setRole("ADMIN");
        verify(employeeRepository).save(target);
        verify(requestRepository).save(request);
    }

    // 5. SUPER_ADMIN rejects a pending request
    @Test
    void rejectShouldRejectPromotionRequest() {

        when(requester.getRole()).thenReturn("SUPER_ADMIN");
        when(requester.getId()).thenReturn(10L);

        AdminPromotionRequest request =
                new AdminPromotionRequest(
                        20L, 10L, "Promotion request"
                );

        when(requestRepository.findByIdForUpdate(100L))
                .thenReturn(Optional.of(request));

        when(requestRepository.save(any(AdminPromotionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminPromotionRequest result =
                promotionService.reject(100L, "Not eligible");

        assertEquals(Status.REJECTED, result.getStatus());
        assertEquals(10L, result.getReviewedById());

        verify(requestRepository).save(request);
        verify(employeeRepository, never()).save(target);
    }

    // 6. EMPLOYEE cannot access promotion requests
    @Test
    void employeeShouldNotAccessPromotionRequests() {

        when(requester.getRole()).thenReturn("EMPLOYEE");

        assertThrows(
                AccessDeniedException.class,
                () -> promotionService.getMyRequests()
        );

        verify(requestRepository, never())
                .findByRequestedByIdOrderByCreatedAtDesc(anyLong());
    }

    // 7. Target employee does not exist
    @Test
    void requestPromotionShouldFailWhenTargetEmployeeNotFound() {

        when(requester.getRole()).thenReturn("ADMIN");

        when(entityManager.find(
                eq(Employee.class),
                eq(20L),
                eq(LockModeType.PESSIMISTIC_WRITE)
        )).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.requestPromotion(20L, "Performance")
        );

        verify(requestRepository, never())
                .save(any(AdminPromotionRequest.class));
    }

    // 8. Target employee is inactive
    @Test
    void requestPromotionShouldFailWhenTargetEmployeeIsInactive() {

        when(requester.getRole()).thenReturn("ADMIN");

        when(target.getRole()).thenReturn("EMPLOYEE");
        when(target.getEmploymentStatus()).thenReturn("INACTIVE");

        when(entityManager.find(
                eq(Employee.class),
                eq(20L),
                eq(LockModeType.PESSIMISTIC_WRITE)
        )).thenReturn(target);

        assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.requestPromotion(20L, "Performance")
        );

        verify(requestRepository, never())
                .save(any(AdminPromotionRequest.class));
    }

    // 9. Target employee is already an ADMIN
    @Test
    void requestPromotionShouldFailWhenTargetEmployeeIsAlreadyAdmin() {

        when(requester.getRole()).thenReturn("ADMIN");

        when(target.getRole()).thenReturn("ADMIN");

        when(entityManager.find(
                eq(Employee.class),
                eq(20L),
                eq(LockModeType.PESSIMISTIC_WRITE)
        )).thenReturn(target);

        assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.requestPromotion(20L, "Performance")
        );

        verify(requestRepository, never())
                .save(any(AdminPromotionRequest.class));
    }

    // 10. Promotion request does not exist

    @Test
    void approveShouldFailWhenPromotionRequestNotFound() {

        when(requester.getRole()).thenReturn("SUPER_ADMIN");

        when(requestRepository.findByIdForUpdate(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.approve(100L, "Approved")
        );

        verify(requestRepository, never())
                .save(any(AdminPromotionRequest.class));
    }



    // 11. Promotion request has already been reviewed
    @Test
    void approveShouldFailWhenRequestIsAlreadyReviewed() {

        when(requester.getRole()).thenReturn("SUPER_ADMIN");

        AdminPromotionRequest request =
                new AdminPromotionRequest(20L, 10L, "Performance");

        request.recordDecision(
                Status.REJECTED, 10L, "Not eligible"
        );

        when(requestRepository.findByIdForUpdate(100L))
                .thenReturn(Optional.of(request));

        assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.approve(100L, "Approved")
        );

        verify(requestRepository, never())
                .save(any(AdminPromotionRequest.class));
    }

    // 12. Reject operation when request does not exist
    @Test
    void rejectShouldFailWhenPromotionRequestNotFound() {

        when(requester.getRole()).thenReturn("SUPER_ADMIN");

        when(requestRepository.findByIdForUpdate(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.reject(100L, "Not eligible")
        );

        verify(requestRepository, never())
                .save(any(AdminPromotionRequest.class));
    }
}
