package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Response.AdressResponseDto;
import com.example.online_shop_api.Entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public static AdressResponseDto toDto(Address address) {
        AdressResponseDto addressResponseDto = new AdressResponseDto();
        addressResponseDto.setId(address.getId());
        addressResponseDto.setCity(address.getCity().getName());
        addressResponseDto.setStreetName(address.getStreetName());
        addressResponseDto.setAdditionalInformation(address.getAdditionalInformation());

        return addressResponseDto;
    }
}
