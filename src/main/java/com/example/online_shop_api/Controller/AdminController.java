package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/show-employees")
    public ResponseEntity<List<Employee>> showEmployees(){
        return adminService.getAllEmployees();
    }
    @PutMapping("/update-employee/{id}")
    public ResponseEntity<Boolean> updateEmployeeSalary(@PathVariable("id") Long id, @RequestParam boolean isEnabled, @RequestParam("salary") String salary){
        return adminService.updateEmployeeStatusAndSalary(id, isEnabled, salary);
    }

}
