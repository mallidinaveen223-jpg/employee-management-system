
package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.entity.EmployeeCodeRegistry;
import com.naveen.employeemanagement.repository.EmployeeCodeRegistryRepository;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EmployeeCodeGeneratorService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeCodeRegistryRepository registryRepository;

    private final SecureRandom random = new SecureRandom();

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    public EmployeeCodeGeneratorService(
            EmployeeRepository employeeRepository,
            EmployeeCodeRegistryRepository registryRepository) {

        this.employeeRepository = employeeRepository;
        this.registryRepository = registryRepository;
    }

    public String generateEmployeeCode() {

        String employeeCode;

        do {
            employeeCode = generateRandomCode();

        } while (
                employeeRepository.existsByEmployeeCode(employeeCode)
                        || registryRepository.existsById(employeeCode)
        );

        // Permanently reserve the generated Employee Code
        registryRepository.saveAndFlush(
                new EmployeeCodeRegistry(employeeCode)
        );

        return employeeCode;
    }

    private String generateRandomCode() {

        List<Character> characters = new ArrayList<>();

        // Randomly select 1 or 2 uppercase letters
        int letterCount = random.nextInt(2) + 1;

        for (int i = 0; i < letterCount; i++) {
            characters.add(
                    LETTERS.charAt(random.nextInt(LETTERS.length()))
            );
        }

        // Remaining characters are digits
        int digitCount = 5 - letterCount;

        for (int i = 0; i < digitCount; i++) {
            characters.add(
                    DIGITS.charAt(random.nextInt(DIGITS.length()))
            );
        }

        // Randomize character positions
        Collections.shuffle(characters, random);

        StringBuilder code = new StringBuilder();

        for (Character character : characters) {
            code.append(character);
        }

        return code.toString();
    }
}
