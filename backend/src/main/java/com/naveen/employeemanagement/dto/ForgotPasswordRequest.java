package com.naveen.employeemanagement.dto;

public class ForgotPasswordRequest {

    private String email;
    private String mobile;

    public ForgotPasswordRequest(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
