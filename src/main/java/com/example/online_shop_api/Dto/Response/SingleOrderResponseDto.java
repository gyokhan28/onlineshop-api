package com.example.online_shop_api.Dto.Response;

import com.example.online_shop_api.Entity.OrderProduct;
import lombok.Data;

import java.util.List;

@Data
public class SingleOrderResponseDto {
    private List<OrderProduct> products;
    private UserResponseDto user;
}
