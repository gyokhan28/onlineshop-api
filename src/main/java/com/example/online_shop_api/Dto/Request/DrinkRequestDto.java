package com.example.online_shop_api.Dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DrinkRequestDto extends ProductRequestDto {
    @NotNull(message = "Best before date cannot be null!")
    private LocalDate bestBefore;
}
