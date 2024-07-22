package com.example.online_shop_api.Exceptions;

public class BrandNotExistException extends RuntimeException {
  public BrandNotExistException(Long id) {
    super("Brand with ID: " + id + " not exist");
  }
}
