package com.example.online_shop_api.Service;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final EmployeeRepository employeeRepository;

    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findByRole_IdNot(1L));
    }

    public ResponseEntity<?> updateEmployeeStatusAndSalary(Long employeeId, Boolean isEnabled, String salary) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        // Get rid of this option to make the method simpler to read
        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        Employee employee = optionalEmployee.get();

        // Check if there's an actual value passed (in case it's only a salary update)
        if (isEnabled != null) {
            employee.setEnabled(isEnabled);
        }

        if (salary != null && !salary.isEmpty()) {
            // Check if the String passed in as a salary is actually a BigDecimal
            try {
                BigDecimal salaryValue = new BigDecimal(salary);
                employee.setSalary(salaryValue);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid salary format");
            }
        }

        employeeRepository.save(employee);

        return ResponseEntity.ok("Employee updated successfully");
    }
}
