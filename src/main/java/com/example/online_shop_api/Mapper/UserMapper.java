package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Dto.Response.UserResponseDto;
import com.example.online_shop_api.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserResponseDto toDto(User user){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setRole(user.getRole());
        userResponseDto.setCreatedAt(user.getCreatedAt());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        userResponseDto.setAddress(user.getAddress());

        return userResponseDto;
    }
}
