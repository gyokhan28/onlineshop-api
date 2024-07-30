package com.example.online_shop_api.Dto.Response;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String imageLocation;
    private Color color;
    private Brand brand;
}
