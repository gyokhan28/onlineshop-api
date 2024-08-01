package com.example.online_shop_api.Dto.Response;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import java.math.BigDecimal;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String productType;
    private BigDecimal price;
    private int quantity;
    private String imageLocation;
    private Color color;
    private Brand brand;
}
