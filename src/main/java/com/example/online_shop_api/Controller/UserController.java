package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Dto.Response.*;
import com.example.online_shop_api.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(UserRequestDto userRequestDto, BindingResult bindingResult) {
        return userService.registerNewUser(userRequestDto, bindingResult);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> showProfile(Authentication authentication) {
        return userService.viewProfile(authentication);
    }

    @GetMapping("/basket/show")
    public ResponseEntity<BasketResponse> showBasket(Authentication authentication) {
        return userService.getBasket(authentication);
    }

    @PostMapping("/basket/buy")
    public ResponseEntity<BuyNowResponse> buyNow(Authentication authentication) {
        return userService.buyNow(authentication);
    }

    @PutMapping("/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestParam("productId") Long productId,
                                            @RequestParam("orderId") Long orderId,
                                            @RequestParam("quantity") int quantity) {
        return userService.updateQuantity(productId, orderId, quantity);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<UserOrdersResponse>> getCurrentUserOrders(Long userId) {
        return userService.getCurrentUserOrders(userId);
    }

    @PutMapping("/cancel-order/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable("id") Long orderId, Authentication authentication){
        return userService.changeOrderStatusToCancelled(orderId, authentication);
    }

    @GetMapping("/profile/edit")
    public ResponseEntity<UserEditResponse> getCurrentUserData(Authentication authentication){
        return userService.getCurrentUserToRequest(authentication);
    }

    @PutMapping("/profile/edit")
    public ResponseEntity<?> editProfile(BindingResult bindingResult, @RequestBody @Valid UserEditResponse userEditResponse, Authentication authentication) {
        return userService.editUserProfile(bindingResult, userEditResponse, authentication);
    }
}
