package com.example.online_shop_api.Dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductCreationRequestDto {
    private String productType;
    private ProductRequestDto productRequestDto;
}
