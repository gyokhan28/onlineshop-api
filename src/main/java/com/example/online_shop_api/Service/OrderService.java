package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Response.OrderResponseDto;
import com.example.online_shop_api.Dto.Response.SingleOrderResponseDto;
import com.example.online_shop_api.Dto.Response.UserResponseDto;
import com.example.online_shop_api.Entity.Order;
import com.example.online_shop_api.Entity.OrderProduct;
import com.example.online_shop_api.Entity.OrderStatus;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    public ResponseEntity<OrderResponseDto> showOrders() {
        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setProducts(productRepository.findByIsDeletedFalse());
        responseDto.setOrderProducts(orderProductRepository.findAll());
        responseDto.setUsers(userRepository.findAll());
        responseDto.setOrders(orderRepository.findAll());
        responseDto.setStatuses(orderStatusRepository.findAll());
        return ResponseEntity.ok(responseDto);
    }

    private void setDeliveryDateTimeIfStatusIsDelivered(Order order) {
        if (order.getStatus().getName().equalsIgnoreCase("DELIVERED")) {
            order.setOrderDeliveryDateTime(LocalDateTime.now());
        }
    }

    public ResponseEntity<Boolean> changeOrderStatus(Long orderId, Long statusId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findById(statusId);

        if (orderOptional.isPresent() && orderStatusOptional.isPresent()) {
            Order order = orderOptional.get();
            OrderStatus status = orderStatusOptional.get();
            order.setStatus(status);
            setDeliveryDateTimeIfStatusIsDelivered(order);
            orderRepository.save(order);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }
    public ResponseEntity<?> viewSingleOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Order order = orderOptional.get();
        User user = order.getUser();
        UserResponseDto userResponseDto = UserMapper.toDto(user);

        List<OrderProduct> orderProductsList = orderProductRepository.findAllByOrderId(orderId);

        SingleOrderResponseDto responseDto = new SingleOrderResponseDto();
        responseDto.setProducts(orderProductsList);
        responseDto.setUser(userResponseDto);
        return ResponseEntity.ok(responseDto);
    }
}
