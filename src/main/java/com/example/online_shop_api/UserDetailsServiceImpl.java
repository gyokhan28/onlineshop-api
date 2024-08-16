package com.example.online_shop_api;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws BadCredentialsException {
        try {
            return loadUserByEmailOrUsername(usernameOrEmail);
        } catch (BadCredentialsException e) {
            return loadEmployeeByEmailOrUsername(usernameOrEmail);
        }
    }

    private UserDetails loadUserByEmailOrUsername(String emailOrUsername) {
        Optional<User> optionalUser = userRepository.findByEmail(emailOrUsername);
        if (optionalUser.isPresent()) {
            return new MyUserDetails(optionalUser.get());
        }
        optionalUser = userRepository.findByUsername(emailOrUsername);
        if (optionalUser.isPresent()) {
            return new MyUserDetails(optionalUser.get());
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    private UserDetails loadEmployeeByEmailOrUsername(String emailOrUsername) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(emailOrUsername);
        if (optionalEmployee.isPresent()) {
            if (!optionalEmployee.get().isEnabled()) {
                throw new DisabledException("User is not enabled");
            }
            return new MyUserDetails(optionalEmployee.get());
        }
        optionalEmployee = employeeRepository.findByUsername(emailOrUsername);
        if (optionalEmployee.isPresent()) {
            return new MyUserDetails(optionalEmployee.get());
        }
        // Throw a generic exception so that hackers won't be able to brute-force attack
        throw new BadCredentialsException("Invalid credentials");
    }
}
