package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import com.example.online_shop_api.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByUsername_UserFoundByEmail() {
        String email = "test@abv.bg";
        User user = new User();
        user.setEmail(email);
        user.setUsername("testUsername");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals("testUsername", userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_UserFoundByUsername() {
        User user = new User();
        String username = "testUsername";
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals("testUsername", userDetails.getUsername());
    }

    @Test
    void testLoadEmployeeByUsername_EmployeeFoundByEmail() {
        String email = "test@abv.bg";
        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setUsername("testUsername");
        employee.setEnabled(true);
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals("testUsername", userDetails.getUsername());
    }

    @Test
    void testLoadEmployeeByUsername_EmployeeFoundByUsername() {
        Employee employee = new Employee();
        String username = "testUsername";
        employee.setUsername(username);
        employee.setEnabled(true);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(employee));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals("testUsername", userDetails.getUsername());
    }

    @Test
    void testLoadEmployeeByUsername_NotEnabled_EmployeeFoundByEmail() {
        String email = "test@abv.bg";
        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setUsername("testUsername");
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));

        assertThrows(DisabledException.class, ()-> {
           userDetailsService.loadUserByUsername(email);
        });
    }



    @Test
    void testLoadUserByUsername_UserNotFound() {
        String email = "test@abv.bg";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
    }


}
