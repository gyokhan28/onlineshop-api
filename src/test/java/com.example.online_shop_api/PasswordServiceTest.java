package com.example.online_shop_api;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import com.example.online_shop_api.Service.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {
    @InjectMocks
    private PasswordService passwordService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private MyUserDetails myUserDetails;
    @Mock
    private Authentication authentication;

    @Test
    public void testIsUserWithUser() {
        when(myUserDetails.getUser()).thenReturn(new User());
        assertTrue(passwordService.isUser(myUserDetails));
    }

    @Test
    public void testIsUserWithNull() {
        when(myUserDetails.getUser()).thenReturn(null);
        assertFalse(passwordService.isUser(myUserDetails));
    }

    @Test
    public void testIsCurrentPasswordCorrectWithUser() {
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("encodedPassword");
        when(myUserDetails.getUser()).thenReturn(user);
        when(bCryptPasswordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        assertTrue(passwordService.isCurrentPasswordCorrect(myUserDetails, "rawPassword"));
    }

    @Test
    public void testIsCurrentPasswordCorrectWithEmployee() {
        Employee employee = mock(Employee.class);
        when(employee.getPassword()).thenReturn("encodedPassword");
        when(myUserDetails.getEmployee()).thenReturn(employee);
        when(bCryptPasswordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        assertTrue(passwordService.isCurrentPasswordCorrect(myUserDetails, "rawPassword"));
    }

    @Test
    public void testUpdatePasswordForUser() {
        User user = mock(User.class);
        when(myUserDetails.getUser()).thenReturn(user);
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        passwordService.updatePassword(myUserDetails, "newPassword");

        verify(user).setPassword("encodedNewPassword");
        verify(userRepository).save(user);
    }

    @Test
    public void testUpdatePasswordForEmployee() {
        Employee employee = mock(Employee.class);
        when(myUserDetails.getEmployee()).thenReturn(employee);
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        passwordService.updatePassword(myUserDetails, "newPassword");

        verify(employee).setPassword("encodedNewPassword");
        verify(employeeRepository).save(employee);
    }

    @Test
    public void testChangePasswordSuccess() {
        User user = mock(User.class);
        when(myUserDetails.getUser()).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(bCryptPasswordEncoder.matches("currentPassword", user.getPassword())).thenReturn(true);
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        ResponseEntity<String> response = passwordService.changePassword("currentPassword", "newPassword", "newPassword", authentication);

        assertEquals("The password was changed successfully!", response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(user).setPassword("encodedNewPassword");
        verify(userRepository).save(user);
    }

    @Test
    public void testChangePasswordIncorrectCurrentPassword() {
        User user = mock(User.class);
        when(myUserDetails.getUser()).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(bCryptPasswordEncoder.matches("currentPassword", user.getPassword())).thenReturn(false);

        ResponseEntity<String> response = passwordService.changePassword("currentPassword", "newPassword", "newPassword", authentication);

        assertEquals("Incorrect current password!", response.getBody());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testChangePasswordMismatchedNewPasswords() {
        User user = mock(User.class);
        when(myUserDetails.getUser()).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(bCryptPasswordEncoder.matches("currentPassword", user.getPassword())).thenReturn(true);

        ResponseEntity<String> response = passwordService.changePassword("currentPassword", "newPassword", "differentPassword", authentication);

        assertEquals("New password and confirm password do not match!", response.getBody());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testChangePasswordEmptyNewPassword() {
        User user = mock(User.class);
        when(myUserDetails.getUser()).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(bCryptPasswordEncoder.matches("currentPassword", user.getPassword())).thenReturn(true);

        ResponseEntity<String> response = passwordService.changePassword("currentPassword", "", "", authentication);

        assertEquals("The new password cannot be empty!", response.getBody());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testChangePasswordShortNewPassword() {
        User user = mock(User.class);
        when(myUserDetails.getUser()).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(bCryptPasswordEncoder.matches("currentPassword", user.getPassword())).thenReturn(true);

        ResponseEntity<String> response = passwordService.changePassword("currentPassword", "pw", "pw", authentication);

        assertEquals("The new password cannot be less than 3 symbols long!", response.getBody());
        assertEquals(400, response.getStatusCode().value());
    }

}
