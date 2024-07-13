package com.example.online_shop_api.Static;

import lombok.Getter;

@Getter
public enum ProductCategory {

    FOOD(1),
    DRINK(2),
    SANITARY(3),
    RAILING(4),
    ACCESSORIES(5),
    DECORATION(6),
    OTHERS(7);

    private final long id;

    ProductCategory(long id) {
        this.id = id;
    }

}

