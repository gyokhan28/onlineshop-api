package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Response.OrderResponseDto;
import com.example.online_shop_api.Entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public static OrderResponseDto toDto (Order order){
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setPrice(order.getPrice());
        dto.setOrderDateTime(order.getOrderDateTime());
        dto.setStatus(order.getStatus().getName());
        return dto;
    }
}
