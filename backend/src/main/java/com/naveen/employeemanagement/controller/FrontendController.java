package com.naveen.employeemanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({
            "/",
            "/register",
            "/dashboard",
            "/employee-dashboard"
    })
    public String serveFrontend() {
        return "forward:/index.html";
    }
}