package com.naveen.employeemanagement.security;

import com.naveen.employeemanagement.entity.Employee;
import com.naveen.employeemanagement.repository.EmployeeRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(
            EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Employee not found with email: " + email
                        )
                );

        String[] roles = switch (employee.getRole()) {
            case "SUPER_ADMIN" ->
                    new String[]{"SUPER_ADMIN", "ADMIN"};
            case "ADMIN" ->
                    new String[]{"ADMIN"};
            case "EMPLOYEE" ->
                    new String[]{"EMPLOYEE"};
            default ->
                    throw new UsernameNotFoundException(
                            "Unsupported employee role."
                    );
        };

        return User.builder()
                .username(employee.getEmail())
                .password(employee.getPasswordHash())
                .roles(roles)
                .build();
    }
}