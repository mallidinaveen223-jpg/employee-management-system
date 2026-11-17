package com.naveen.employeemanagement.controller;

import com.naveen.employeemanagement.entity.AdminPromotionRequest;
import com.naveen.employeemanagement.service.AdminPromotionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/promotions")
public class AdminPromotionController {

    private final AdminPromotionService promotionService;

    public AdminPromotionController(
            AdminPromotionService promotionService) {

        this.promotionService = promotionService;
    }

    // ADMIN submits a request.
    // SUPER_ADMIN's own request is approved immediately.
    @PostMapping("/requests")
    public ResponseEntity<AdminPromotionRequest> requestPromotion(
            @Valid @RequestBody PromotionRequestBody body) {

        AdminPromotionRequest request =
                promotionService.requestPromotion(
                        body.employeeId(),
                        body.reason()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(request);
    }

    // Admin can view requests they submitted.
    @GetMapping("/mine")
    public List<AdminPromotionRequest> getMyRequests() {
        return promotionService.getMyRequests();
    }

    // Only SUPER_ADMIN can view the pending review queue.
    @GetMapping("/pending")
    public List<AdminPromotionRequest> getPendingRequests() {
        return promotionService.getPendingRequests();
    }

    // Only SUPER_ADMIN can approve.
    @PostMapping("/{requestId}/approve")
    public AdminPromotionRequest approve(
            @PathVariable Long requestId,
            @Valid @RequestBody(required = false)
            ReviewRequestBody body) {

        return promotionService.approve(
                requestId,
                body == null ? null : body.comment()
        );
    }

    // Only SUPER_ADMIN can reject.
    @PostMapping("/{requestId}/reject")
    public AdminPromotionRequest reject(
            @PathVariable Long requestId,
            @Valid @RequestBody(required = false)
            ReviewRequestBody body) {

        return promotionService.reject(
                requestId,
                body == null ? null : body.comment()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "message",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(
            IllegalArgumentException exception) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(
            IllegalStateException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "message",
                        exception.getMessage()
                ));
    }

    public record PromotionRequestBody(

            @NotNull(message = "Employee ID is required.")
            @Positive(message = "Employee ID must be positive.")
            Long employeeId,

            @NotBlank(message = "Reason is required.")
            @Size(
                    max = 1000,
                    message = "Reason cannot exceed 1000 characters."
            )
            String reason
    ) {
    }

    public record ReviewRequestBody(

            @Size(
                    max = 1000,
                    message = "Comment cannot exceed 1000 characters."
            )
            String comment
    ) {
    }
}