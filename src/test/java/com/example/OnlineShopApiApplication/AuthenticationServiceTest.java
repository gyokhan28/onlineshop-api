package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.LoginDtos.LoginDto;
import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.AuthenticationFailedException;
import com.example.online_shop_api.Mapper.EmployeeMapper;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import com.example.online_shop_api.Service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private MyUserDetails myUserDetails;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testSignupUser_Success() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("testUser");
        userRequestDto.setEmail("test@example.com");
        userRequestDto.setPassword("password");

        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        try (MockedStatic<UserMapper> mockedMapper = mockStatic(UserMapper.class)) {
            mockedMapper.when(() -> UserMapper.toEntity(any(UserRequestDto.class))).thenReturn(user);

            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User result = authenticationService.signup(userRequestDto);

            assertNotNull(result, "The saved user should not be null");
            assertEquals("testUser", result.getUsername());
            assertEquals("encodedPassword", result.getPassword());

            verify(userRepository, times(1)).save(any(User.class));
            mockedMapper.verify(() -> UserMapper.toEntity(any(UserRequestDto.class)), times(1));
        }
    }

    @Test
    void testSignupEmployee_Success() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();
        employeeRequestDto.setUsername("testEmployee");
        employeeRequestDto.setEmail("test@example.com");
        employeeRequestDto.setPassword("password");

        Employee employee = new Employee();
        employee.setUsername("testEmployee");
        employee.setEmail("test@example.com");
        employee.setPassword("encodedPassword");

        try (MockedStatic<EmployeeMapper> mockedMapper = mockStatic(EmployeeMapper.class)) {
            mockedMapper.when(() -> EmployeeMapper.toEntity(any(EmployeeRequestDto.class))).thenReturn(employee);

            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

            Employee result = authenticationService.signup(employeeRequestDto);

            assertNotNull(result);
            assertEquals("testEmployee", result.getUsername());
            assertEquals("encodedPassword", result.getPassword());

            verify(employeeRepository, times(1)).save(any(Employee.class));
            mockedMapper.verify(() -> EmployeeMapper.toEntity(any(EmployeeRequestDto.class)), times(1));
        }
    }

    @Test
    void testAuthenticate_Success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testUser");
        loginDto.setPassword("password");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);

        UserDetails result = authenticationService.authenticate(loginDto);

        assertNotNull(result);
        assertEquals(myUserDetails, result);
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
    }

    @Test
    void testAuthenticate_Fail() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testUser");
        loginDto.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new AuthenticationException("Authentication failed") {});

        assertThrows(AuthenticationFailedException.class, () -> {
            authenticationService.authenticate(loginDto);
        });

        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
    }

}
