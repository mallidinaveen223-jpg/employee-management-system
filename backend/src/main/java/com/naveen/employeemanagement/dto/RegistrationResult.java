
package com.naveen.employeemanagement.dto;

import com.naveen.employeemanagement.entity.RegistrationRequest;

public class RegistrationResult {

    private final RegistrationRequest registrationRequest;
    private final String activationToken;

    public RegistrationResult(
            RegistrationRequest registrationRequest,
            String activationToken) {

        this.registrationRequest = registrationRequest;
        this.activationToken = activationToken;
    }

    public RegistrationRequest getRegistrationRequest() {
        return registrationRequest;
    }

    public String getActivationToken() {
        return activationToken;
    }
}