package com.example.online_shop_api.Controller;


import com.example.online_shop_api.Dto.Response.OrderResponseDto;
import com.example.online_shop_api.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/show")
    public ResponseEntity<OrderResponseDto> showOrders(){
        return orderService.showOrders();
    }
}
