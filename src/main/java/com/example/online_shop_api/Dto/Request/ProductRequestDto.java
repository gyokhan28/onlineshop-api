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
public class ProductRequestDto {
    @Size(min = 2, max = 50, message = "Product name must be between 2 and 50 symbols")
    private String name;

    @NotNull(message = "You must enter a price")
    @DecimalMin(value = "0.10", message = "Price must be greater than or equal to 0.10")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String imageLocation;

//    private String productType; // will be creating different products of this

    private LocalDate expiryDate;  // TODO - make sure date is not expired
    private LocalDate bestBefore; // TODO - make sure date is not expired
    private Material material;
    private Brand brand;
    private Color color;

    private boolean isBiodegradable;
    private boolean isReusable;
    private boolean isNonSlip;
    private boolean isOutdoor;

    public boolean getIsBiodegradable(){
        return this.isBiodegradable;
    }
    public boolean getIsReusable(){
        return this.isReusable;
    }
    public boolean getIsNonSlip(){
        return this.isNonSlip;
    }
    public boolean getIsOutdoor(){
        return this.isOutdoor;
    }
}

