package com.example.online_shop_api.Dto.Response;

import ch.qos.logback.core.status.Status;
import com.example.online_shop_api.Entity.Order;
import com.example.online_shop_api.Entity.OrderProduct;
import com.example.online_shop_api.Entity.OrderStatus;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Entity.User;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDto {
    List<Order> orders;
    List<OrderProduct> orderProducts;
    List<Product> products;
    List<User> users;
    List<OrderStatus> statuses;
}
