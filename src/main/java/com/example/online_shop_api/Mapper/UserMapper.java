package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Dto.Response.UserEditResponse;
import com.example.online_shop_api.Dto.Response.UserResponseDto;
import com.example.online_shop_api.Entity.Address;
import com.example.online_shop_api.Entity.City;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Static.BulgarianCity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toDto(User user) {
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

    public User toEntity(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            return null;
        }
        User.UserBuilder user = User.builder();

        user.firstName(userRequestDto.getFirstName());
        user.lastName(userRequestDto.getLastName());
        user.username(userRequestDto.getUsername());
        user.email(userRequestDto.getEmail());
        user.password(userRequestDto.getPassword());
        user.phoneNumber(userRequestDto.getPhoneNumber());

        user.createdAt(java.time.LocalDateTime.now());
        user.isEnabled(true);
        user.address(createAddress(userRequestDto));

        return user.build();
    }

    public static Address createAddress(UserRequestDto userRequestDto) {
        if (userRequestDto.getCityId() == null) {
            return null;
        }
        City city = BulgarianCity.getCityById(userRequestDto.getCityId());
        if (city == null) {
            return null;
        }
        return Address.builder()
                .city(city)
                .streetName(userRequestDto.getStreetName())
                .additionalInformation(userRequestDto.getAdditionalInformation())
                .build();
    }

    public static UserEditResponse toResponse(User user){
        UserEditResponse response = new UserEditResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setCity(user.getAddress().getCity().getName());
        response.setStreetName(user.getAddress().getStreetName());
        response.setPhoneNumber(user.getPhoneNumber());
        return response;
    }
}
