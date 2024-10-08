package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Response.EmployeeEditResponse;
import com.example.online_shop_api.Dto.Response.EmployeeResponseDto;
import com.example.online_shop_api.Service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/get-all")
    public ResponseEntity<List<EmployeeResponseDto>> listAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/get-all-except-admins")
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployeesExceptAdmins() {
        return employeeService.getAllEmployeesExceptAdmins();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewEmployee(@RequestBody @Valid EmployeeRequestDto employeeRequestDto, BindingResult bindingResult) {
        return employeeService.registerNewEmployee(employeeRequestDto, bindingResult);
    }

    @GetMapping("/profile/edit")
    public ResponseEntity<EmployeeEditResponse> getCurrentEmployeeData(Authentication authentication) {
        return employeeService.getCurrentEmployeeToRequest(authentication);
    }

    @PostMapping("/profile/edit")
    public ResponseEntity<?> editProfile(@RequestBody @Valid EmployeeEditResponse employeeEditResponse, BindingResult bindingResult, Authentication authentication) {
        return employeeService.editEmployeeProfile(bindingResult, employeeEditResponse, authentication);
    }
}
