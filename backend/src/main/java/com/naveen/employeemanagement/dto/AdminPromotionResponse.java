package com.naveen.employeemanagement.dto;

import com.naveen.employeemanagement.entity.AdminPromotionRequest;

import java.time.LocalDateTime;

public record AdminPromotionResponse (

    Long id,
    Long targetEmployeeId,
    Long requestedById,
    String reason,
    AdminPromotionRequest.Status status,
    LocalDateTime createdAt,
    Long reviewedById,
    LocalDateTime reviewedAt,
    String reviewComment
){
    public static AdminPromotionResponse from(
            AdminPromotionRequest request) {

        return new AdminPromotionResponse(
                request.getId(),
                request.getTargetEmployeeId(),
                request.getRequestedById(),
                request.getReason(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getReviewedById(),
                request.getReviewedAt(),
                request.getReviewComment()
        );

    }

}
