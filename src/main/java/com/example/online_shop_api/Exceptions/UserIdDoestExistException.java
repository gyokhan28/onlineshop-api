package com.example.online_shop_api.Exceptions;

public class UserIdDoestExistException extends RuntimeException{
    public UserIdDoestExistException(String message) {
        super(message);
    }
}
