package com.example.online_shop_api.Exceptions;

public class DisabledEmployeeException extends RuntimeException{
    public DisabledEmployeeException(String message) {
        super(message);
    }
}
