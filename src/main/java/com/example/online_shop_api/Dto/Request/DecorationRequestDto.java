package com.example.online_shop_api.Dto.Request;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecorationRequestDto extends ProductRequestDto {
    @NotNull
    private Material material;

    @NotNull
    private Brand brand;
}
