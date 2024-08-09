package com.example.online_shop_api.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserOrdersResponse {
    private Long orderId;
    private LocalDateTime orderDate;
    private List<ProductResponse> products;
    private BigDecimal price;
    private String status;
    private String deliveryAddress;
}
