package com.example.online_shop_api.Dto.Response;

import lombok.Data;

@Data
public class AdressResponseDto {
    private Long id;
    private String city;
    private String streetName;
    private String additionalInformation;
}
