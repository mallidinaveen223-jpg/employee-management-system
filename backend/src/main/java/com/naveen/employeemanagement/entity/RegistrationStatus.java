package com.naveen.employeemanagement.entity;

public enum RegistrationStatus {
    PENDING_ACTIVATION,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,

    // Retained temporarily for compatibility with existing service code
    // while the onboarding flow is migrated.
    PENDING
}
