package com.example.online_shop_api.Static;

import lombok.Getter;

@Getter
public enum JobType {

    ADMIN(0),
    TEST(1),
    CASHIER(2),
    SALES_ASSOCIATE(3),
    SALES_ASSISTANT(4),
    STORE_MANAGER(5);

    private final long id;

    JobType(long id) {
        this.id = id;
    }
}
