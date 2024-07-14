package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Employee>> listAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @GetMapping("/register")
    public ResponseEntity<EmployeeRequestDto> getNewEmployee(){
        return employeeService.getNewEmployee();
    }

}
