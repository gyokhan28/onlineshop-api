package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Response.ErrorResponse;
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

    public ResponseEntity<Boolean> updateEmployeeStatusAndSalary(Long employeeId, boolean isEnabled, String salary){
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if(employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            employee.setEnabled(isEnabled);
            if(salary != null && !salary.isEmpty()){
                BigDecimal salaryValue = new BigDecimal(salary);
                employee.setSalary(salaryValue);
            }
            employeeRepository.save(employee);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }
}