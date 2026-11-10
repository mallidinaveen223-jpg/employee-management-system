package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmployeeCodeGeneratorService {

    private final EmployeeRepository employeeRepository;
    private final SecureRandom random = new SecureRandom();

    private static final String LETTETRS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    public EmployeeCodeGeneratorService (EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }
    public String generateEmployeeCode(){
        String employeeCode;
        do {
            employeeCode = generateRandomCode();
        } while (employeeRepository.existsByEmployeeCode(employeeCode));

        return  employeeCode;
    }
    private String generateRandomCode(){
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 5; i ++){
            if (i == 1 || i == 3){
                code.append(LETTETRS.charAt(random.nextInt(LETTETRS.length())));
            } else {
                code.append((DIGITS.charAt(random.nextInt(DIGITS.length()))));
            }
        }
        return code.toString();
    }
}
