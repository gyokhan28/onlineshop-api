package com.example.online_shop_api.Exceptions;

public class OrderStatusException extends RuntimeException{
    public OrderStatusException(String message) {
        super(message);
    }
}
