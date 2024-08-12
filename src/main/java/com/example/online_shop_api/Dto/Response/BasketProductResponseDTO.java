package com.example.online_shop_api.Dto.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BasketProductResponseDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private List<String> imageUrls;
    private int quantity;
    private BigDecimal subtotal;
}
