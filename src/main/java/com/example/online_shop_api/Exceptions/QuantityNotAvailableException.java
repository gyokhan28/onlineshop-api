package com.example.online_shop_api.Exceptions;

public class QuantityNotAvailableException extends RuntimeException{
    public QuantityNotAvailableException(String message) {
        super(message);
    }
}
