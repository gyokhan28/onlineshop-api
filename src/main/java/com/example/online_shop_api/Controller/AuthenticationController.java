package com.example.online_shop_api.Controller;


import com.example.online_shop_api.Dto.LoginDtos.LoginDto;
import com.example.online_shop_api.Dto.LoginDtos.LoginResponse;
import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Service.AuthenticationService;
import com.example.online_shop_api.Service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>  authenticate(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        UserDetails authenticatedUser = authenticationService.authenticate(loginDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}