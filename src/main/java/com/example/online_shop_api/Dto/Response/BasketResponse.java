package com.example.online_shop_api.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class BasketResponse {
    private List<BasketProductResponseDTO> products;
    private BigDecimal totalPrice;
}
