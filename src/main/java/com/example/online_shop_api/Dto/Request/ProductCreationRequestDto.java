package com.example.online_shop_api.Dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreationRequestDto {
    private String productType;
    private ProductRequestDto productRequestDto;
}
