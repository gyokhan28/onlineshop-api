package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.Request.FoodRequestDto;
import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.Food;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Repository.ProductRepository;
import com.example.online_shop_api.Service.MinioService;
import com.example.online_shop_api.Service.ProductService;
import com.example.online_shop_api.Utils.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    MinioService minioService;
    @Mock
    ValidationUtil validationUtil;
    @InjectMocks
    ProductService productService;
    @Test
    void testGetProduct_Success() throws Exception {
        Product product = new Product();
        Long productId = 1L;
        product.setId(productId);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(productId);
        List<String> mockImageUrls = Arrays.asList("url1", "url2");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);
        when(minioService.listFilesInDirectoryFullPath(productId.toString())).thenReturn(mockImageUrls);

        ResponseEntity<?> response = productService.getProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProductResponseDto responseBody = (ProductResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(mockImageUrls, responseBody.getImageUrls());
    }

    @Test
    void testGetProduct_NotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProduct(productId));
    }

    @Test
    void testGetAllProducts_Success() throws Exception {
        Product mockProduct = new Product();
        mockProduct.setId(1L);

        ProductResponseDto mockResponseDto = new ProductResponseDto();
        mockResponseDto.setId(1L);
        List<String> mockImageUrls = Arrays.asList("url1", "url2");

        when(productRepository.findAll()).thenReturn(Arrays.asList(mockProduct));
        when(modelMapper.map(mockProduct, ProductResponseDto.class)).thenReturn(mockResponseDto);
        when(minioService.listFilesInDirectoryFullPath(mockProduct.getId().toString())).thenReturn(mockImageUrls);

        ResponseEntity<List<ProductResponseDto>> response = productService.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ProductResponseDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(mockImageUrls, responseBody.get(0).getImageUrls());
    }

    @Test
    void testGetAllProducts_EmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ProductResponseDto>> response = productService.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testAddNewProduct_Success() throws Exception {
        String productType = "Food";
        ProductRequestDto productRequestDto = new ProductRequestDto();

        FoodRequestDto specificDto = new FoodRequestDto();
        Food mockProduct = new Food();

        when(modelMapper.map(productRequestDto, FoodRequestDto.class)).thenReturn(specificDto);
        when(validationUtil.validate(specificDto)).thenReturn(Collections.emptyMap());
        when(modelMapper.map(specificDto, Food.class)).thenReturn(mockProduct);

        ResponseEntity<?> response = productService.addNewProduct(productType, productRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product created successfully", response.getBody());
        verify(productRepository).save(mockProduct);
    }
    @Test
    void testAddNewProduct_InvalidProductType() {
        String invalidProductType = "InvalidType";
        ProductRequestDto mockRequestDto = new ProductRequestDto();

        ResponseEntity<?> response = productService.addNewProduct(invalidProductType, mockRequestDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product type not found", response.getBody());
    }
    @Test
    void testAddNewProduct_ValidationErrors() {
        String productType = "Food";
        ProductRequestDto productRequestDto = new ProductRequestDto();

        FoodRequestDto specificDto = new FoodRequestDto();
        Map<String, String> mockErrors = Map.of("field", "error");

        when(modelMapper.map(productRequestDto, FoodRequestDto.class)).thenReturn(specificDto);
        when(validationUtil.validate(specificDto)).thenReturn(mockErrors);

        ResponseEntity<?> response = productService.addNewProduct(productType, productRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(mockErrors, response.getBody());
    }
    @Test
    void testAddNewProduct_InternalServerError() throws Exception {
        String productType = "Food";
        ProductRequestDto productRequestDto = new ProductRequestDto();

        when(modelMapper.map(any(), any())).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = productService.addNewProduct(productType, productRequestDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", response.getBody());
    }

}
