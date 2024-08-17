package com.example.online_shop_api.Dto.Request;

import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SanitaryRequestDto extends ProductRequestDto {
    @NotNull
    private Boolean isBiodegradable;

    @NotNull
    private Boolean isReusable;

    @NotNull
    private Material material;
}
