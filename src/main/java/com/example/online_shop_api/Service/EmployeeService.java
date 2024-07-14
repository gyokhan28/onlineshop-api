package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public ResponseEntity<List<Employee>> getAllEmployees() {
        return employeeRepository.findByRole_IdNot(1L);
    }

    public ResponseEntity<EmployeeRequestDto> getNewEmployee(){
        return ResponseEntity.ok(new EmployeeRequestDto());
    }

}
