package com.example.OnlineShopApiApplication.Dto;

import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


 class  ProductResponseDtoTest {

    @Test
    void testBuilder() {
        // Arrange
        Long expectedId = 1L;
        String expectedName = "Product";
        BigDecimal expectedPrice = new BigDecimal("10.00");
        int expectedQuantity = 5;
        String expectedImageLocation = "src/main/resources/image.png";
        Color expectedColor = new Color(1L, "Red");
        Brand expectedBrand = new Brand(1L, "Nike");

        // Act
        ProductResponseDto dto = ProductResponseDto.builder()
                .id(expectedId)
                .name(expectedName)
                .price(expectedPrice)
                .quantity(expectedQuantity)
                .imageLocation(expectedImageLocation)
                .color(expectedColor)
                .brand(expectedBrand)
                .build();

        // Assert
        assertEquals(expectedId, dto.getId());
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedPrice, dto.getPrice());
        assertEquals(expectedQuantity, dto.getQuantity());
        assertEquals(expectedImageLocation, dto.getImageLocation());
        assertEquals(expectedColor, dto.getColor());
        assertEquals(expectedBrand, dto.getBrand());
    } }
