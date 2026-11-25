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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import  com.naveen.employeemanagement.dto.AdminPromotionResponse;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/admin/promotions")
public class AdminPromotionController {

    private final AdminPromotionService promotionService;

    public AdminPromotionController(
            AdminPromotionService promotionService) {

        this.promotionService = promotionService;
    }


    @PostMapping("/requests")
    public ResponseEntity<AdminPromotionResponse> requestPromotion(
            @Valid @RequestBody PromotionRequestBody body) {

        AdminPromotionRequest request = promotionService.requestPromotion(
                body.employeeId(),
                body.reason()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AdminPromotionResponse.from(request));
    }

    @GetMapping("/mine")
    public List<AdminPromotionResponse> getMyRequests() {
        return promotionService.getMyRequests()
                .stream()
                .map(AdminPromotionResponse::from)
                .toList();
    }

    @GetMapping("/pending")
    public List<AdminPromotionResponse> getPendingRequests() {
        return promotionService.getPendingRequests()
                .stream()
                .map(AdminPromotionResponse::from)
                .toList();
    }

    @PostMapping("/{requestId}/approve")
    public AdminPromotionResponse approve(
            @PathVariable
            @Positive(message = "Requset ID must be positive.")
            Long requestId,
            @Valid @RequestBody(required = false) ReviewRequestBody body) {

        AdminPromotionRequest request = promotionService.approve(
                requestId,
                body == null ? null : body.comment()
        );

        return AdminPromotionResponse.from(request);
    }

    @PostMapping("/{requestId}/reject")
    public AdminPromotionResponse reject(
            @PathVariable
            @Positive(message = "Request ID must be positive.")
            Long requestId,
            @Valid @RequestBody(required = false) ReviewRequestBody body) {

        AdminPromotionRequest request = promotionService.reject(
                requestId,
                body == null ? null : body.comment()
        );

        return AdminPromotionResponse.from(request);
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
    @ExceptionHandler(
            org.springframework.web.bind.MethodArgumentNotValidException.class)

    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception){

     String message = exception.getBindingResult()
             .getFieldErrors()
             .stream()
             .findFirst()
             .map(error -> error.getDefaultMessage())
             .orElse("Invalid request.");

     return ResponseEntity
             .badRequest()
             .body(Map.of("message", message));
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