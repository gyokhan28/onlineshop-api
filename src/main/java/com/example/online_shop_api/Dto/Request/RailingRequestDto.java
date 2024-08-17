package com.example.online_shop_api.Dto.Request;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RailingRequestDto extends ProductRequestDto {
    @NotNull
    private Material material;

    @NotNull
    private Color color;

    @NotNull
    private Brand brand;
}
