package com.example.online_shop_api.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {
    private String name;
    private int quantity;
}
