package com.example.online_shop_api.Services;

import com.example.online_shop_api.Dto.LoginDtos.LoginEmployeeDto;
import com.example.online_shop_api.Dto.LoginDtos.LoginUserDto;
import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmployeeRepository employeeRepository;

    public User signup(UserRequestDto input) {
        User user = UserMapper.toEntity(input);
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        return userRepository.save(user);
    }

    public Employee signup(EmployeeRequestDto input) {
        Employee employee = new Employee();
        employee.setUsername(input.getUsername());
        employee.setEmail(input.getEmail());
        employee.setPassword(passwordEncoder.encode(input.getPassword()));
        return employeeRepository.save(employee);
    }

    public User authenticate(LoginUserDto input) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (input.getUsername(), input.getPassword()));
        } catch (
                AuthenticationException e) {
            // Log the error or handle it accordingly
            throw new RuntimeException("Authentication failed", e);
        }
        return userRepository.findByEmail(input.getUsername()).orElseThrow();
    }

//    public User authenticate(LoginUserDto input) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        input.getUsername(),
//                        input.getPassword()
//                )
//        );
//        return userRepository.findByUsername(input.getUsername())
//                .orElseThrow();
//    }

    public Employee authenticate(LoginEmployeeDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return employeeRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}