package com.example.online_shop_api.Exceptions;

public class ColorNotExistException extends RuntimeException {
    public ColorNotExistException(Long id) {
        super("Color with ID: " + id + " not exist");
    }

}
