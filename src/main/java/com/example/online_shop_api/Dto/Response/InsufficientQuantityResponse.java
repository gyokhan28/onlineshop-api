package com.example.online_shop_api.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsufficientQuantityResponse {
    private String productName;
    private int requestedQuantity;
    private int stockQuantity;
}
