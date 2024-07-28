package com.example.online_shop_api.Dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileResponse {
    private UserResponseDto userResponseDto;
    private List<OrderResponseDto> orderList;
}
