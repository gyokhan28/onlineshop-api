package com.example.online_shop_api.Controller;


import com.example.online_shop_api.Dto.Response.OrderResponseDto;
import com.example.online_shop_api.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/show")
    public ResponseEntity<OrderResponseDto> showOrders(){
        return orderService.showOrders();
    }

    @PutMapping("/change-status")
    public ResponseEntity<Boolean> changeOrderStatus(@RequestParam Long orderId, @RequestParam Long statusId){
        return orderService.changeOrderStatus(orderId, statusId);
    }
}
