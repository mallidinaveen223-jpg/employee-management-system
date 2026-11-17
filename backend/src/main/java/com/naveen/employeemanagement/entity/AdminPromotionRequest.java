package com.naveen.employeemanagement.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "admin_promotion_requests",
        indexes = {
                @Index(
                        name = "idx_promotion_status_created",
                        columnList = "status,created_at"
                ),
                @Index(
                        name = "idx_promotion_target",
                        columnList = "target_employee_id"
                )
        }
)
public class AdminPromotionRequest {

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Employee being proposed for admin access.
    @Column(name = "target_employee_id", nullable = false, updatable = false)
    private Long targetEmployeeId;

    // Admin who submitted the request.
    @Column(name = "requested_by_id", nullable = false, updatable = false)
    private Long requestedById;

    @Column(nullable = false, length = 1000, updatable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Super admin who made the final decision.
    @Column(name = "reviewed_by_id")
    private Long reviewedById;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_comment", length = 1000)
    private String reviewComment;

    @Version
    private Long version;

    protected AdminPromotionRequest() {
        // Required by JPA.
    }

    public AdminPromotionRequest(
            Long targetEmployeeId,
            Long requestedById,
            String reason) {

        if (targetEmployeeId == null || requestedById == null) {
            throw new IllegalArgumentException(
                    "Target employee and requesting admin are required."
            );
        }

        if (reason == null
                || reason.isBlank()
                || reason.trim().length() > 1000) {
            throw new IllegalArgumentException(
                    "Reason must contain between 1 and 1000 characters."
            );
        }

        this.targetEmployeeId = targetEmployeeId;
        this.requestedById = requestedById;
        this.reason = reason.trim();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void recordDecision(
            Status decision,
            Long reviewerId,
            String comment) {

        if (status != Status.PENDING) {
            throw new IllegalStateException(
                    "This request has already been reviewed."
            );
        }

        if (decision != Status.APPROVED
                && decision != Status.REJECTED) {
            throw new IllegalArgumentException(
                    "Decision must be APPROVED or REJECTED."
            );
        }

        if (reviewerId == null) {
            throw new IllegalArgumentException(
                    "Reviewer is required."
            );
        }

        if (comment != null && comment.trim().length() > 1000) {
            throw new IllegalArgumentException(
                    "Review comment cannot exceed 1000 characters."
            );
        }

        this.status = decision;
        this.reviewedById = reviewerId;
        this.reviewedAt = LocalDateTime.now();
        this.reviewComment = comment == null ? null : comment.trim();
    }

    public Long getId() {
        return id;
    }

    public Long getTargetEmployeeId() {
        return targetEmployeeId;
    }

    public Long getRequestedById() {
        return requestedById;
    }

    public String getReason() {
        return reason;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getReviewedById() {
        return reviewedById;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public Long getVersion() {
        return version;
    }
}