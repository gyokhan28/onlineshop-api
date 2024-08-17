package com.example.online_shop_api.Dto.Request;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UpdateProductRequestDto {
    private String name;

    private BigDecimal price;

    private Integer quantity;

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
