package com.example.online_shop_api.Exceptions;

public class PasswordsNotMatchingException extends RuntimeException{
    public PasswordsNotMatchingException(String message) {
        super(message);
    }
}
