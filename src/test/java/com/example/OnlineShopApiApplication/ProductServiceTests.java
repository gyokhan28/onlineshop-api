package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Repository.Products.ProductRepository;
import com.example.online_shop_api.Service.Products.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
 class ProductServiceTests {
  private ProductService productService;
  @Mock private ProductRepository productRepository;
  @Mock private ModelMapper modelMapper;

  private Product testProduct;

  @BeforeEach
  public void setUp() {
    productService = new ProductService(productRepository, modelMapper);

    testProduct =
        Product.builder()
            .id(1L)
            .name("product")
            .price(new BigDecimal(10))
            .quantity(1)
            .imageUrls(List.of("src/main/resources/"))
            .build();
  }

  @Test
   void testGetAllProducts() {
    List<Product> productList = List.of(testProduct);

    when(productRepository.findAll()).thenReturn(productList);
    ResponseEntity<List<ProductResponseDto>> response = productService.getAllProducts();

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(productList.size(), response.getBody().size());

    verify(productRepository, times(1)).findAll();
  }
}
