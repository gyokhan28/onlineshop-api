package com.example.online_shop_api.Dto.Request;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessoryRequestDto extends ProductRequestDto {
    @NotNull(message = "Color must be entered!")
    private Color color;
    @NotNull(message = "Brand must be entered!")
    private Brand brand;
}
