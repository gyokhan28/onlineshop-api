package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Dto.Response.*;
import com.example.online_shop_api.Entity.City;
import com.example.online_shop_api.Repository.CityRepository;
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
    private final CityRepository cityRepository;

    @GetMapping("/register")
    public ResponseEntity<List<City>> getAllCities(){
        return ResponseEntity.ok(cityRepository.findAll());
    }
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
    public ResponseEntity<?> buyNow(Authentication authentication) {
        return userService.buyNow(authentication);
    }

    @PutMapping("/basket/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestParam("productId") Long productId,
                                            @RequestParam("quantity") int quantity,
                                            Authentication authentication) throws Exception {
        return userService.updateQuantity(productId, quantity, authentication);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<UserOrdersResponse>> getCurrentUserOrders(Authentication authentication) {
        return userService.getCurrentUserOrders(authentication);
    }

    @PutMapping("/cancel-order/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable("id") Long orderId, Authentication authentication){
        return userService.cancelOrder(orderId, authentication);
    }

    @GetMapping("/profile/edit")
    public ResponseEntity<UserEditResponse> getCurrentUserData(Authentication authentication){
        return userService.getCurrentUserToRequest(authentication);
    }

    @PostMapping("/profile/edit")
    public ResponseEntity<?> editProfile(@RequestBody @Valid UserEditResponse userEditResponse, BindingResult bindingResult, Authentication authentication) {
        return userService.editUserProfile(bindingResult, userEditResponse, authentication);
    }
}
