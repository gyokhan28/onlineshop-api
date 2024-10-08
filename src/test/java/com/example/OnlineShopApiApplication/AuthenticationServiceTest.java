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
class AuthenticationServiceTest {
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
