package com.example.OnlineShopApiApplication.Dto;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Entity.Products.Accessory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessoryTest {

  @Test
  void testGettersAndSetters() {
    // Arrange
    Color color = new Color(1L, "Red");
    Brand brand = new Brand(1L, "Nike");

    Accessory accessory = new Accessory();
    accessory.setColor(color);
    accessory.setBrand(brand);

    // Act & Assert
    assertEquals(color, accessory.getColor());
    assertEquals(brand, accessory.getBrand());
  }
}
