package com.example.online_shop_api.Exceptions;

public class EmailInUseException extends RuntimeException{
    public EmailInUseException(String message) {
        super(message);
    }
}
