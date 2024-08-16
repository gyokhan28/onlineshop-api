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
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody UserRequestDto userRequestDto, BindingResult bindingResult) {
        return userService.registerNewUser(userRequestDto, bindingResult);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> showProfile(Authentication authentication) {
        return userService.viewProfile(authentication);
    }

    @GetMapping("/basket/show")
    public ResponseEntity<BasketResponse> showBasket(Authentication authentication) throws Exception {
        return userService.getBasket(authentication);
    }

    @GetMapping("/basket/buy")
    public ResponseEntity<?> buyNow(@RequestParam("userId") Long userId) {
        return userService.buyNow(userId);
    }

    @PutMapping("/basket/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestParam("productId") Long productId,
                                            @RequestParam("quantity") int quantity,
                                            Authentication authentication) throws Exception {
        return userService.updateQuantity(productId, quantity, authentication);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<UserOrdersResponse>> getCurrentUserOrders(Long userId) {
        return userService.getCurrentUserOrders(userId);
    }

    @PutMapping("/cancel-order/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable("id") Long orderId, Authentication authentication){
        return userService.cancelOrder(orderId, authentication);
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