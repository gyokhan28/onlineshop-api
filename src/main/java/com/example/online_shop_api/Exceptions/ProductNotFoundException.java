package com.example.online_shop_api.Exceptions;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id) {
        super("Accessories with ID: " + id + " not found");
    }
}
