package com.example.online_shop_api.Exceptions;

public class ProductStockNotEnoughException extends RuntimeException{
    public ProductStockNotEnoughException(String message) {
        super(message);
    }
}
