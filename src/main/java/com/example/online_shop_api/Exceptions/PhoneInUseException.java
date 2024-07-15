package com.example.online_shop_api.Exceptions;

public class PhoneInUseException extends RuntimeException{
    public PhoneInUseException(String message) {
        super(message);
    }
}
