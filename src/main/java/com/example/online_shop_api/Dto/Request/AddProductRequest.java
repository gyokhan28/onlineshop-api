package com.example.online_shop_api.Dto.Request;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import lombok.Data;

import java.util.List;

@Data
public class AddProductRequest {
    private String productType;
    private ProductRequestDto productRequestDto;
    private List<Material> materials;
    private List<Color> colors;
    private List<Brand> brands;
}
