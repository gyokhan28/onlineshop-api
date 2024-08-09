package com.example.online_shop_api.Services;

import com.example.online_shop_api.Dto.LoginDtos.LoginDto;
import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
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

    public UserDetails authenticate(LoginDto loginDto, boolean isEmployee) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
            Authentication authResult = authenticationManager.authenticate(authentication);

            return (MyUserDetails) authResult.getPrincipal();
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }
}