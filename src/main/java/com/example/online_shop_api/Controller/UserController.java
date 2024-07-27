package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(UserRequestDto userRequestDto, BindingResult bindingResult){
        return userService.registerNewUser(userRequestDto, bindingResult);
    }

}
