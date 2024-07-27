package com.example.online_shop_api.Exceptions;

public class CityNotFoundException extends RuntimeException{
    public CityNotFoundException(String message) {
        super(message);
    }
}
