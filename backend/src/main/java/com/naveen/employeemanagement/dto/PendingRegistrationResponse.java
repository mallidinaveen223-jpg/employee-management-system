package com.naveen.employeemanagement.dto;

import java.time.LocalDateTime;

public class PendingRegistrationResponse {

    private Long id;
    private String fullName;
    private String email;
    private String mobile;
    private String status;
    private LocalDateTime createdAt;

    public PendingRegistrationResponse(
            Long id, String fullName, String email,String mobile,
            String status, LocalDateTime createdAt){

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
