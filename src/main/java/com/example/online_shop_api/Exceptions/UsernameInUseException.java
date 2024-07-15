package com.example.online_shop_api.Exceptions;

public class UsernameInUseException extends RuntimeException{
    public UsernameInUseException(String message) {
        super(message);
    }
}
