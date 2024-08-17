package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Response.OrderProductResponseDto;
import com.example.online_shop_api.Dto.Response.OrderResponseDto;
import com.example.online_shop_api.Entity.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public static OrderResponseDto toDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setUserResponseDto(UserMapper.toDto(order.getUser()));
        dto.setOrderDateTime(order.getOrderDateTime());
        dto.setOrderDeliveryDateTime(order.getOrderDeliveryDateTime());
        dto.setOrderCancelDateTime(order.getOrderCancelDateTime());
        dto.setStatus(order.getStatus().getName());
        List<OrderProductResponseDto> orderProductResponseDtos = new ArrayList<>();

        order.getProducts().forEach(orderProduct -> {
            OrderProductResponseDto orDto = OrderProductMapper.toDto(orderProduct);
            orderProductResponseDtos.add(OrderProductMapper.toDto(orderProduct));
        });

        dto.setOrderProducts(orderProductResponseDtos);
        return dto;
    }
}
