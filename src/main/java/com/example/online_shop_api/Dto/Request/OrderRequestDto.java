package com.example.online_shop_api.Dto.Request;

import com.example.online_shop_api.Entity.OrderProduct;
import com.example.online_shop_api.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDto {
    private User user;
    private List<OrderProduct> orderProducts; // with quantity

    // the User will be adding item 1 by one in the shopping cart.
    // разглежда продукти - цъка добави в количка (количката = list<product> - те отиват във USER-a му.
    // после когато създаде ордър - те отиват тук и от тук във Service class за да видим дали всички продукти са все още налични
    // от там връщаме грешки на отделните редове..

}
