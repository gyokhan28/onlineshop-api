package com.example.OnlineShopApiApplication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.online_shop_api.Repository.BrandRepository;
import com.example.online_shop_api.Repository.ColorRepository;
import com.example.online_shop_api.Repository.MaterialRepository;
import com.example.online_shop_api.Repository.Products.ProductRepository;
import com.example.online_shop_api.Service.MinioService;
import com.example.online_shop_api.Service.Products.ProductService;
import com.example.online_shop_api.Utils.ValidationUtil;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
 class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    MaterialRepository materialRepository;
    @Mock
    ColorRepository colorRepository;
    @Mock
    BrandRepository brandRepository;
    @Mock
    MinioService minioService;
    @Mock
    ValidationUtil validationUtil;
    @InjectMocks
    ProductService productService;

    @Test
    void testGetProduct_NotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProduct(productId));
    }


}
