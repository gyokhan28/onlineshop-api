package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Repository.BrandRepository;
import com.example.online_shop_api.Repository.ColorRepository;
import com.example.online_shop_api.Service.Products.ProductHelpersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class ProductHelpersServiceTests {

  private ProductHelpersService productHelpersService;

  @Mock private BrandRepository brandRepository;

  @Mock private ColorRepository colorRepository;

  @BeforeEach
  void setUp() {
    productHelpersService = new ProductHelpersService(brandRepository, colorRepository);
  }

  @Test
  void testGetAllBrand() {
    // Arrange
    Brand brand1 = new Brand();
    brand1.setId(1L);
    brand1.setName("Brand1");

    Brand brand2 = new Brand();
    brand2.setId(2L);
    brand2.setName("Brand2");

    List<Brand> brandList = List.of(brand1, brand2);

    when(brandRepository.findAll()).thenReturn(brandList);

    // Act
    List<Brand> result = productHelpersService.getAllBrand();

    // Assert
    assertEquals(2, result.size());
    assertEquals(brand1, result.get(0));
    assertEquals(brand2, result.get(1));

    verify(brandRepository, times(1)).findAll();
  }

  @Test
  void testGetAllColors() {
    // Arrange
    Color color1 = new Color();
    color1.setId(1L);
    color1.setName("Color1");

    Color color2 = new Color();
    color2.setId(2L);
    color2.setName("Color2");

    List<Color> colorList = List.of(color1, color2);

    when(colorRepository.findAll()).thenReturn(colorList);

    // Act
    List<Color> result = productHelpersService.getAllColors();

    // Assert
    assertEquals(2, result.size());
    assertEquals(color1, result.get(0));
    assertEquals(color2, result.get(1));

    verify(colorRepository, times(1)).findAll();
  }
}
