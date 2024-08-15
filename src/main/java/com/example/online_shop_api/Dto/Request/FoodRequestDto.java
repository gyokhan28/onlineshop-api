package com.example.online_shop_api.Dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FoodRequestDto extends ProductRequestDto {
    @NotNull(message = "Expiry date cannot be null!")
    private LocalDate expiryDate;
}
