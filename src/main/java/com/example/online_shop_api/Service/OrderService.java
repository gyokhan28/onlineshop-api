package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Response.OrderResponseDto;
import com.example.online_shop_api.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    @Autowired
    public OrderService(ProductRepository productRepository,
                        OrderProductRepository orderProductRepository,
                        UserRepository userRepository,
                        OrderRepository orderRepository,
                        OrderStatusRepository orderStatusRepository) {
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    public ResponseEntity<OrderResponseDto> showOrders(){
        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setProducts(productRepository.findByIsDeletedFalse());
        responseDto.setOrderProducts(orderProductRepository.findAll());
        responseDto.setUsers(userRepository.findAll());
        responseDto.setOrders(orderRepository.findAll());
        responseDto.setStatuses(orderStatusRepository.findAll());
        return ResponseEntity.ok(responseDto);
    }
}
