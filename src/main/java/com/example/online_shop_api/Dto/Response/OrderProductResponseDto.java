package com.example.online_shop_api.Dto.Response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProductResponseDto {
    private Long id;
    private String productName;
    private int quantity;
    private BigDecimal productPriceWhenPurchased;
}
