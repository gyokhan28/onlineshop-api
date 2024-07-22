package com.example.online_shop_api.Exceptions;

public class AccessoriesNotFoundException extends RuntimeException{
    public AccessoriesNotFoundException(Long id) {
        super("Accessories with ID: " + id + " not found");
    }
}
