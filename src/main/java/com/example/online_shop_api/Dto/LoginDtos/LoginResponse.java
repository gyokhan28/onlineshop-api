package com.example.online_shop_api.Dto.LoginDtos;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;

    private long expiresIn;
}
