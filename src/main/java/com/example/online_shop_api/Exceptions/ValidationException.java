package com.example.online_shop_api.Exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException (String message) {
        super(message);
    }
}
