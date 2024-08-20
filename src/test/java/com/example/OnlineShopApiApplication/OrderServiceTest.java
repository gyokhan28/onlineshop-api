package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.Response.OrderResponseDto;
import com.example.online_shop_api.Dto.Response.SingleOrderResponseDto;
import com.example.online_shop_api.Dto.Response.UserResponseDto;
import com.example.online_shop_api.Entity.*;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Mapper.OrderMapper;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.Repository.OrderProductRepository;
import com.example.online_shop_api.Repository.OrderRepository;
import com.example.online_shop_api.Repository.OrderStatusRepository;
import com.example.online_shop_api.Service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowOrders() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.builder().name("CASHIER").build());

        OrderProduct op1 = new OrderProduct();
        op1.setProduct(new Product());
        OrderProduct op2 = new OrderProduct();
        op2.setProduct(new Product());
        // Arrange
        Order order1 = new Order();
        order1.setStatus(OrderStatus.builder().name("PENDING").build());
        order1.setUser(user);
        order1.setProducts(List.of(op1,op2));
        Order order2 = new Order();
        order2.setStatus(OrderStatus.builder().name("PENDING").build());
        order2.setUser(user);
        order2.setProducts(List.of(op1,op2));
        List<Order> orderList = Arrays.asList(order1, order2);

        OrderResponseDto orderResponseDto1 = new OrderResponseDto();
        OrderResponseDto orderResponseDto2 = new OrderResponseDto();
        List<OrderResponseDto> expectedResponseList = Arrays.asList(orderResponseDto1, orderResponseDto2);

        when(orderRepository.findAll()).thenReturn(orderList);
        try (MockedStatic<OrderMapper> orderMapperMockedStatic = mockStatic(OrderMapper.class)) {
            orderMapperMockedStatic.when(() -> OrderMapper.toDto(order1)).thenReturn(orderResponseDto1);
            orderMapperMockedStatic.when(() -> OrderMapper.toDto(order2)).thenReturn(orderResponseDto2);

            ResponseEntity<List<OrderResponseDto>> responseEntity = orderService.showOrders();

            assertEquals(ResponseEntity.ok(expectedResponseList), responseEntity);
        }
    }

    @Test
    void testChangeOrderStatus_Success() {
        Long orderId = 1L;
        Long statusId = 2L;

        Order order = new Order();
        OrderStatus status = new OrderStatus();
        status.setName("PENDING");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderStatusRepository.findById(statusId)).thenReturn(Optional.of(status));

        ResponseEntity<Boolean> response = orderService.changeOrderStatus(orderId, statusId);

        assertEquals(ResponseEntity.ok(true), response);
        assertEquals(status, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testChangeOrderStatus_OrderNotFound() {
        Long orderId = 1L;
        Long statusId = 2L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(orderStatusRepository.findById(statusId)).thenReturn(Optional.of(new OrderStatus()));

        ResponseEntity<Boolean> response = orderService.changeOrderStatus(orderId, statusId);
        assertEquals(ResponseEntity.ok(false), response);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testChangeOrderStatus_StatusNotFound() {
        Long orderId = 1L;
        Long statusId = 2L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order()));
        when(orderStatusRepository.findById(statusId)).thenReturn(Optional.empty());

        ResponseEntity<Boolean> response = orderService.changeOrderStatus(orderId, statusId);

        assertEquals(ResponseEntity.ok(false), response);
        verify(orderRepository, never()).save(any(Order.class)); // Ensure save was not called
    }

    @Test
    void testViewSingleOrder_Success() {
        Long orderId = 1L;

        User user = new User();
        Order order = new Order();
        order.setUser(user);

        List<OrderProduct> orderProductsList = List.of(new OrderProduct(), new OrderProduct());
        UserResponseDto userResponseDto = new UserResponseDto();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderProductRepository.findAllByOrderId(orderId)).thenReturn(orderProductsList);

        try (MockedStatic<UserMapper> userMapperMockedStatic = mockStatic(UserMapper.class)) {
            userMapperMockedStatic.when(() -> UserMapper.toDto(user)).thenReturn(userResponseDto);

            ResponseEntity<?> response = orderService.viewSingleOrder(orderId);

            assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
            SingleOrderResponseDto responseDto = (SingleOrderResponseDto) response.getBody();
            assert responseDto != null;
            assertEquals(orderProductsList, responseDto.getProducts());
            assertEquals(userResponseDto, responseDto.getUser());
        }
    }
    @Test
    void testViewSingleOrder_OrderNotFound() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = orderService.viewSingleOrder(orderId);

        assertEquals(ResponseEntity.notFound().build().getStatusCode(), response.getStatusCode());
        verify(orderProductRepository, never()).findAllByOrderId(anyLong());
    }
}
