package com.example.online_shop_api.Static;

import lombok.Getter;

@Getter
public enum RoleType {
    ROLE_ADMIN(1),
    ROLE_EMPLOYEE(2),
    ROLE_USER(3);
    private final long id;

    RoleType(long id) {
        this.id = id;
    }
}
