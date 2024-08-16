package com.example.online_shop_api.Exceptions;

public class UserIdDoesNotExistException extends RuntimeException{
    public UserIdDoesNotExistException(String message) {
        super(message);
    }
}
