package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.Request.FoodRequestDto;
import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Request.UpdateProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Order;
import com.example.online_shop_api.Entity.OrderStatus;
import com.example.online_shop_api.Entity.Products.Food;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.ServerErrorException;
import com.example.online_shop_api.Repository.*;
import com.example.online_shop_api.Service.MinioService;
import com.example.online_shop_api.Service.OrderService;
import com.example.online_shop_api.Service.ProductService;
import com.example.online_shop_api.Utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private ProductRequestDto productRequestDto = new ProductRequestDto();
    @Mock
    private ProductRepository productRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    OrderService orderService;
    @Mock
    Validator validator;
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
    void testAddNewProduct_Success() {
        String productType = "Food";

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

    @Test
    void testAddNewProduct_UnknownProductType() {
        productRequestDto = new ProductRequestDto();

        String productType = "InvalidType";

        ResponseEntity<?> response = productService.addNewProduct(productType, productRequestDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product type not found", response.getBody());
    }

    @Test
    void testAddNewProduct_CorrectProductType() {
        String productType = "Food";
        ProductRequestDto productRequestDto = new ProductRequestDto();

        FoodRequestDto foodRequestDto = new FoodRequestDto();
        Food food = new Food();

        when(modelMapper.map(eq(productRequestDto), eq(FoodRequestDto.class))).thenReturn(foodRequestDto);
        when(modelMapper.map(eq(foodRequestDto), eq(Food.class))).thenReturn(food);
        when(validationUtil.validate(any())).thenReturn(new HashMap<>());

        ResponseEntity<?> response = productService.addNewProduct(productType, productRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product created successfully", response.getBody());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        assertNotNull(productCaptor.getValue());
        assertEquals(Food.class, productCaptor.getValue().getClass());
    }

    @Test
    void testValidateProduct_NoViolations() {
        ProductRequestDto validProductRequestDto = new ProductRequestDto(); // Запълнете с валидни данни
        when(validator.validate(validProductRequestDto)).thenReturn(Set.of());

        String result = productService.validateProduct(validProductRequestDto);

        assertEquals(null, result, "The result should be null when there are no violations");
    }

    @Test
    void testValidateProduct_WithViolations() {
        ProductRequestDto invalidProductRequestDto = new ProductRequestDto();

        Set<ConstraintViolation<ProductRequestDto>> violations = new HashSet<>();

        ConstraintViolation<ProductRequestDto> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<ProductRequestDto> violation2 = mock(ConstraintViolation.class);

        when(violation1.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation1.getPropertyPath().toString()).thenReturn("name");
        when(violation1.getMessage()).thenReturn("must not be empty");

        when(violation2.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation2.getPropertyPath().toString()).thenReturn("price");
        when(violation2.getMessage()).thenReturn("must be positive");

        violations.add(violation1);
        violations.add(violation2);

        when(validator.validate(invalidProductRequestDto)).thenReturn(violations);

        String result = productService.validateProduct(invalidProductRequestDto);

        assertTrue(result.contains("name: must not be empty"), "The result should contain 'name: must not be empty'");
        assertTrue(result.contains("price: must be positive"), "The result should contain 'price: must be positive'");
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        Long productId = 1L;
        UpdateProductRequestDto updateProductRequestDto = new UpdateProductRequestDto();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = productService.updateProduct(updateProductRequestDto, productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product with id " + productId + " not found", response.getBody());
    }

    @Test
    void testUpdateProduct_ValidationErrors() {
        Long productId = 1L;
        UpdateProductRequestDto updateProductRequestDto = new UpdateProductRequestDto();
        Product existingProduct = new Product();

        Set<ConstraintViolation<ProductRequestDto>> violations = new HashSet<>();
        ConstraintViolation<ProductRequestDto> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("name");
        when(violation.getMessage()).thenReturn("must not be empty");
        violations.add(violation);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(validator.validate(any(ProductRequestDto.class))).thenReturn(violations);

        ResponseEntity<?> response = productService.updateProduct(updateProductRequestDto, productId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation errors: name: must not be empty", response.getBody());
    }

    @Test
    void testUpdateProduct_Success() {
        Long productId = 1L;
        UpdateProductRequestDto updateProductRequestDto = new UpdateProductRequestDto();
        Product existingProduct = new Product();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(validator.validate(any(ProductRequestDto.class))).thenReturn(new HashSet<>());

        ResponseEntity<?> response = productService.updateProduct(updateProductRequestDto, productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product updated successfully", response.getBody());
        verify(productRepository).save(existingProduct);
    }

    @Test
    void testUpdateProduct_Exception() {
        Long productId = 1L;
        UpdateProductRequestDto updateProductRequestDto = new UpdateProductRequestDto();
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));

        doThrow(new RuntimeException("An error occurred")).when(productRepository).save(any(Product.class));

        ResponseEntity<?> response = productService.updateProduct(updateProductRequestDto, productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: An error occurred", response.getBody());
    }

    @Test
    void testGetBasketOrder_OneBasketOrder() {
        // Arrange
        User user = new User();
        user.setId(1L);

        OrderStatus basketOrderStatus = new OrderStatus();
        basketOrderStatus.setId(1L);
        basketOrderStatus.setName("BASKET");

        Order order = new Order();
        order.setId(1L);
        order.setStatus(basketOrderStatus);
        order.setUser(user);

        List<Order> basketOrders = Collections.singletonList(order);

        when(orderRepository.findAllByUser_IdAndStatus_Id(user.getId(), 0L)).thenReturn(basketOrders);

        Optional<Order> result = productService.getBasketOrder(user);

        assertTrue(result.isPresent(), "The result should be present when there is one basket order");
        assertEquals(order, result.get(), "The result should be the order returned from the repository");
    }

    @Test
    void testGetBasketOrder_NoBasketOrder() {
        User user = new User();
        user.setId(1L);

        OrderStatus basketOrderStatus = new OrderStatus();
        basketOrderStatus.setId(1L);
        basketOrderStatus.setName("BASKET");

        lenient().when(orderRepository.findAllByUser_IdAndStatus_Id(user.getId(), 0L))
                .thenReturn(Collections.emptyList());

        Optional<Order> result = productService.getBasketOrder(user);

        assertFalse(result.isPresent(), "The result should be empty when there are no basket orders");
    }

    @Test
    void testGetBasketOrder_MoreThanOneBasketOrder() {
        User user = new User();
        user.setId(1L);

        OrderStatus basketOrderStatus = new OrderStatus();
        basketOrderStatus.setId(1L);
        basketOrderStatus.setName("BASKET");

        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(basketOrderStatus);
        order1.setUser(user);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(basketOrderStatus);
        order2.setUser(user);

        List<Order> basketOrders = Arrays.asList(order1, order2);

        lenient().when(orderRepository.findAllByUser_IdAndStatus_Id(user.getId(), 0L))
                .thenReturn(basketOrders);

        ServerErrorException exception = assertThrows(ServerErrorException.class, () -> {
            productService.getBasketOrder(user);
        });

        assertEquals("Critical server error. More than one basket for user with userID: " + user.getId(), exception.getMessage());
    }
}