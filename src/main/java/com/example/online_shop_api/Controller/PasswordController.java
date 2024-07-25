package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @PutMapping("/save")
    public ResponseEntity<String> changePassword(@RequestParam("currentPassword") String currentPassword,
                                                  @RequestParam("newPassword") String newPassword,
                                                  @RequestParam("confirmPassword") String repeatPassword,
                                                  Authentication authentication){
        return passwordService.changePassword(currentPassword, newPassword, repeatPassword, authentication);
    }
}
