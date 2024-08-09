package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.LoginDtos.LoginDto;
import com.example.online_shop_api.Dto.LoginDtos.LoginResponse;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDto loginDto) {
        UserDetails authenticatedUser = authenticationService.authenticate(loginDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}