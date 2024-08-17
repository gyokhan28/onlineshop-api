package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.LoginDtos.LoginDto;
import com.example.online_shop_api.Exceptions.AuthenticationFailedException;
import com.example.online_shop_api.Exceptions.DisabledEmployeeException;
import com.example.online_shop_api.MyUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;

    public UserDetails authenticate(LoginDto loginDto) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
            Authentication authResult = authenticationManager.authenticate(authentication);
            return (MyUserDetails) authResult.getPrincipal();
        } catch (DisabledException e) {
            throw new DisabledEmployeeException("This account is not activated yet.");
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Authentication failed: Invalid username or password.");
        }
    }
}