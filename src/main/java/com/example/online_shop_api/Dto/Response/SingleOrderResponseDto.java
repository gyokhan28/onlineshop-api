package com.example.online_shop_api.Dto.Response;

import com.example.online_shop_api.Entity.Order;
import com.example.online_shop_api.Entity.OrderProduct;
import com.example.online_shop_api.Entity.User;
import lombok.Data;

import java.util.List;

@Data
public class SingleOrderResponseDto {
    private Order order;
    private User user;
    private List<OrderProduct> orderProductList;
}
