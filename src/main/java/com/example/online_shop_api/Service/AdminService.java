package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Response.EmployeeResponseDto;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Mapper.EmployeeMapper;
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

    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        List<Employee> employeeList = employeeRepository.findByRole_IdNot(1L);

        return ResponseEntity.ok(EmployeeMapper.toDtoList(employeeList));
    }

    public ResponseEntity<String> updateEmployeeStatusAndSalary(Long employeeId, Boolean isEnabled, String salary) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        Employee employee = optionalEmployee.get();

        if (isEnabled != null) {
            employee.setEnabled(isEnabled);
        }

        if (salary != null && !salary.isEmpty()) {
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
