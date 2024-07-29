package com.example.online_shop_api.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BuyNowResponse {
    private boolean success;
    private List<String> errors;
    private BigDecimal totalPrice;
    private Long orderId;
}
