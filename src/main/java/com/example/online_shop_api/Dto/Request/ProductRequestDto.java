package com.example.online_shop_api.Dto.Request;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductRequestDto {
    @Size(min = 2, max = 50, message = "Product name must be between 2 and 50 symbols")
    private String name;

    @NotNull(message = "You must enter a price")
    @DecimalMin(value = "0.10", message = "Price must be greater than or equal to 0.10")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private LocalDate expiryDate; // TODO - make sure date is not expired
    private LocalDate bestBefore; // TODO - make sure date is not expired
    private Material material;
    private Brand brand;
    private Color color;

    private Boolean isBiodegradable;
    private Boolean isReusable;
    private Boolean isNonSlip;
    private Boolean isOutdoor;
}

