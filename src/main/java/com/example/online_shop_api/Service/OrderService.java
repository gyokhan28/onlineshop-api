package com.example.online_shop_api.Service;

import ch.qos.logback.core.status.Status;
import com.example.online_shop_api.Dto.Response.OrderResponseDto;
import com.example.online_shop_api.Entity.Order;
import com.example.online_shop_api.Entity.OrderStatus;
import com.example.online_shop_api.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    private void setDeliveryDateTimeIfStatusIsDelivered(Order order){
        if(order.getStatus().getName().equalsIgnoreCase("DELIVERED")){
            order.setOrderDeliveryDateTime(LocalDateTime.now());
        }
    }

    public ResponseEntity<Boolean> changeOrderStatus(Long orderId, Long statusId){
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findById(statusId);

        if(orderOptional.isPresent() && orderStatusOptional.isPresent()){
            Order order = orderOptional.get();
            OrderStatus status = orderStatusOptional.get();
            order.setStatus(status);
            setDeliveryDateTimeIfStatusIsDelivered(order);
            orderRepository.save(order);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }
}
