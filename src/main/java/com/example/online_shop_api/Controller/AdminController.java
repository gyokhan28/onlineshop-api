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

    @PutMapping("/enable-employee/{id}")
    public ResponseEntity<Boolean> enableEmployee(@PathVariable("id") Long employeeId){
        return adminService.enableEmployee(employeeId);
    }

    @PutMapping("/disable-employee/{id}")
    public ResponseEntity<Boolean> disableEmployee(@PathVariable("id") Long employeeId){
        return adminService.disableEmployee(employeeId);
    }

    @PutMapping("/update-salary/{id}")
    public ResponseEntity<Boolean> updateEmployeeSalary(@PathVariable("id") Long id, @RequestParam("salary") String salary){
        return adminService.updateEmployeeSalary(id, salary);
    }

}
