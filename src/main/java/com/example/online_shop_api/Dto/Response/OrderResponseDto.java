package com.example.online_shop_api.Dto.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {
    private Long id;
    private LocalDateTime orderDateTime;
    private String status;
    private BigDecimal price;
}
