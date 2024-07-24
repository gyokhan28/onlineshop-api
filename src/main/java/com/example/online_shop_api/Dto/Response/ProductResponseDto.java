package com.example.online_shop_api.Dto.Response;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Data
@JsonInclude(NON_DEFAULT)
public class ProductResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private List<String> imageUrls;

    private LocalDate bestBefore;
    private LocalDate expiryDate;
    private Color color;
    private Brand brand;
    private Material material;
    private boolean isOutdoor;
    private boolean isNonSlip;
    private boolean isBiodegradable;
    private boolean isReusable;
}
