package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public static ProductResponseDto toDto(Product product){
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setQuantity(product.getQuantity());
        responseDto.setName(product.getName());
        responseDto.setPrice(product.getPrice());
        responseDto.setImageUrls(product.getImageUrls());
        return responseDto;
    }
}
