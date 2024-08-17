package com.example.online_shop_api.Controller;


import com.example.online_shop_api.Dto.LoginDtos.LoginDto;
import com.example.online_shop_api.Dto.LoginDtos.LoginResponse;
import com.example.online_shop_api.Exceptions.AuthenticationFailedException;
import com.example.online_shop_api.Exceptions.DisabledEmployeeException;
import com.example.online_shop_api.Service.AuthenticationService;
import com.example.online_shop_api.Service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> authenticate(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        UserDetails userDetails;
        try {
            userDetails = authenticationService.authenticate(loginDto);
        } catch (DisabledEmployeeException | AuthenticationFailedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        String jwtToken = jwtService.generateToken(userDetails);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}