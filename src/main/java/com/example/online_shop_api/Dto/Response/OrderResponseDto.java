package com.example.online_shop_api.Dto.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private UserResponseDto userResponseDto;
    private LocalDateTime orderDateTime;
    private LocalDateTime orderDeliveryDateTime;
    private LocalDateTime orderCancelDateTime;
    private String status;
    private List<OrderProductResponseDto> orderProducts;
    private BigDecimal price;
}
