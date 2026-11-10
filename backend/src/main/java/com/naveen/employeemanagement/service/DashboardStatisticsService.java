package com.naveen.employeemanagement.service;

import com.naveen.employeemanagement.entity.RegistrationStatus;
import com.naveen.employeemanagement.repository.EmployeeRepository;
import com.naveen.employeemanagement.repository.RegistrationRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardStatisticsService {

    private final EmployeeRepository employeeRepository;
    private final RegistrationRequestRepository registrationRequestRepository;

    public DashboardStatisticsService(EmployeeRepository employeeRepository,
                                      RegistrationRequestRepository registrationRequestRepository) {
        this.employeeRepository = employeeRepository;
        this.registrationRequestRepository = registrationRequestRepository;
    }

    public Map<String, Object> getDashbordStatistics(){

        Map<String, Object> statistics = new LinkedHashMap<>();

        LocalDateTime today = LocalDateTime.now();

        LocalDateTime monthStart = today
                .withDayOfMonth(1)
                .toLocalDate().atStartOfDay();

        LocalDateTime monthEnd = today
                .withDayOfMonth(today.toLocalDate().lengthOfMonth())
                .toLocalDate().atTime(23, 59, 59, 999999999);

        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByEmploymentStatus("ACTIVE");
        long inactiveEmployees = employeeRepository.countByEmploymentStatus("INACTIVE");
        long pendingRegistrations = registrationRequestRepository.countByStatus(RegistrationStatus.PENDING);
        long newJoinersThisMonth = employeeRepository.countByJoiningDateBetween(monthStart, monthEnd);

        statistics.put("totalEmployees", totalEmployees);
        statistics.put("activeEmployees", activeEmployees);
        statistics.put("inactiveEmployees", inactiveEmployees);
        statistics.put("pendingRegistrations", pendingRegistrations);
        statistics.put("employeesOnLeave", null);
        statistics.put("newJoinersThisMonth", newJoinersThisMonth);

       Map<String, Long> departmentSatatistics = new LinkedHashMap<>();

       for (Object[] row : employeeRepository.getDepartmentWiseEmployeeCount()){
           String department = String.valueOf(row[0]);
           long employeeCount = Long.parseLong(String.valueOf(row[1]));
           departmentSatatistics.put(department, employeeCount);
       }
       statistics.put("departmentStatistics", departmentSatatistics);
       return statistics;
    }
}
