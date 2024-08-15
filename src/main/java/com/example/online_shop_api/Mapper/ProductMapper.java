package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Response.BasketProductResponseDTO;
import com.example.online_shop_api.Entity.Products.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public static BasketProductResponseDTO toDto(Product product){
        BasketProductResponseDTO responseDto = new BasketProductResponseDTO();
        responseDto.setQuantity(product.getQuantity());
        responseDto.setName(product.getName());
        responseDto.setPrice(product.getPrice());
        return responseDto;
    }
}
