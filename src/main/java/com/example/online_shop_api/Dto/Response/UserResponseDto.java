package com.example.online_shop_api.Dto.Response;

import com.example.online_shop_api.Entity.Address;
import com.example.online_shop_api.Entity.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private boolean isEnabled;
    private String phoneNumber;
    private AdressResponseDto address;
}
