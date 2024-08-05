package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Response.OrderProductResponseDto;
import com.example.online_shop_api.Entity.OrderProduct;
import org.springframework.stereotype.Component;

@Component
public class OrderProductMapper {
    public static OrderProductResponseDto toDto(OrderProduct orderProduct) {
        OrderProductResponseDto dto = new OrderProductResponseDto();
        dto.setId(orderProduct.getId());
        dto.setProductName(orderProduct.getProduct().getName());
        dto.setQuantity(orderProduct.getQuantity());
        dto.setProductPriceWhenPurchased(orderProduct.getProductPriceWhenPurchased());

        return dto;
    }
}
