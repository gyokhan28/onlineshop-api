package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.LoginDtos.LoginEmployeeDto;
import com.example.online_shop_api.Dto.LoginDtos.LoginResponse;
import com.example.online_shop_api.Dto.LoginDtos.LoginUserDto;
import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Services.AuthenticationService;
import com.example.online_shop_api.Services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("test-1");
    }

    @PostMapping("/user/signup")
    public ResponseEntity<User> register(@RequestBody UserRequestDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/employee/signup")
    public ResponseEntity<Employee> register(@RequestBody EmployeeRequestDto registerUserDto) {
        Employee registeredEmployee = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredEmployee);
    }

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken((UserDetails) authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/employee/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginEmployeeDto loginEmployeeDto) {
        Employee authenticatedEmployee = authenticationService.authenticate(loginEmployeeDto);

        String jwtToken = jwtService.generateToken((UserDetails) authenticatedEmployee);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}