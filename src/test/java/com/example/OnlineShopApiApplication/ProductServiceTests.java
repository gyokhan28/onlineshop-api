package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.Request.ProductCreationRequestDto;
import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Entity.Products.Accessory;
import com.example.online_shop_api.Entity.Products.Decoration;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Exceptions.ProductNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductServiceTests {
  private ProductService productService;
  @Mock private ProductRepository productRepository;
  @Mock private ModelMapper modelMapper;

  private Product testProduct;
  private ProductResponseDto testProductResponseDto;
  private ProductCreationRequestDto productCreationRequestDto;
  private ProductResponseDto productResponseDto;

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

  productCreationRequestDto = ProductCreationRequestDto.builder()
          .productType("Accessory")
          .productRequestDto(ProductRequestDto.builder()
                  .color(new Color(1L,"Black"))
                  .brand(new Brand(1L,"Nike"))
                  .imageLocation("src/main/resources/image.png")
                  .build())
          .build();

  productResponseDto = ProductResponseDto.builder()
          .id(1L)
          .name("Product")
          .price(new BigDecimal("10.00"))
          .quantity(1)
          .imageLocation("src/main/resources/image.png")
          .build();
}

  @Test
  void testCreateProduct_Accessory() {
    Product accessoryProduct = new Accessory();
    accessoryProduct.setId(1L);
    accessoryProduct.setName("Accessory Product");
    accessoryProduct.setPrice(new BigDecimal("10.00"));
    accessoryProduct.setQuantity(1);
    accessoryProduct.setImageUrls(List.of("src/main/resources/image.png"));

    productCreationRequestDto.setProductType("Accessory");
    when(modelMapper.map(any(ProductRequestDto.class), eq(Accessory.class))).thenReturn(new Accessory());
    when(productRepository.save(accessoryProduct)).thenReturn(accessoryProduct);
    when(modelMapper.map(accessoryProduct, ProductResponseDto.class)).thenReturn(productResponseDto);

    ResponseEntity<ProductResponseDto> response = productService.create(productCreationRequestDto);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(productResponseDto, response.getBody());

    verify(productRepository, times(1)).save(accessoryProduct);
    verify(modelMapper, times(1)).map(any(ProductRequestDto.class), eq(Accessory.class));
    verify(modelMapper, times(1)).map(accessoryProduct, ProductResponseDto.class);
  }

  @Test
  void testCreateProduct_Decoration() {
    Product decorationProduct = new Decoration();
    decorationProduct.setId(1L);
    decorationProduct.setName("Decoration Product");
    decorationProduct.setPrice(new BigDecimal("15.00"));
    decorationProduct.setQuantity(2);
    decorationProduct.setImageUrls(List.of("src/main/resources/image.png"));

    // Mock the behavior
    productCreationRequestDto.setProductType("Decoration");
    when(modelMapper.map(any(ProductRequestDto.class), eq(Decoration.class))).thenReturn(new Decoration());
    when(productRepository.save(decorationProduct)).thenReturn(decorationProduct);
    when(modelMapper.map(decorationProduct, ProductResponseDto.class)).thenReturn(productResponseDto);

    ResponseEntity<ProductResponseDto> response = productService.create(productCreationRequestDto);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(productResponseDto, response.getBody());

    verify(productRepository, times(1)).save(decorationProduct);
    verify(modelMapper, times(1)).map(any(ProductRequestDto.class), eq(Decoration.class));
    verify(modelMapper, times(1)).map(decorationProduct, ProductResponseDto.class);
  }

  @Test
  void testCreateProduct_InvalidType() {
    productCreationRequestDto.setProductType("InvalidType");

    IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
      productService.create(productCreationRequestDto);
    });

    assertEquals("Invalid product type: InvalidType", thrownException.getMessage());

    verify(productRepository, times(0)).save(any(Product.class));
    verify(modelMapper, times(0)).map(any(ProductRequestDto.class), any(Class.class));
  }

  @Test
  void testGetById() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
    when(modelMapper.map(testProduct, ProductResponseDto.class)).thenReturn(testProductResponseDto);

    ProductResponseDto response = productService.getById(1L);

    assertEquals(testProductResponseDto, response);

    verify(productRepository, times(1)).findById(1L);
    verify(modelMapper, times(1)).map(testProduct, ProductResponseDto.class);
  }

  @Test
  void testGetById_ProductNotFound() {
    doThrow(new ProductNotFoundException(1L)).when(productRepository).findById(1L);

    assertThrows(
        ProductNotFoundException.class,
        () -> {
          productService.getById(1L);
        });

    verify(productRepository, times(1)).findById(1L);

    verify(modelMapper, times(0)).map(any(), any());
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

  @Test
  void testUpdateProduct_Success() {
    // Arrange
    Product existingProduct = Product.builder()
            .id(1L)
            .name("Old Product")
            .price(new BigDecimal("5.00"))
            .quantity(1)
            .imageUrls(List.of("src/main/resources/old_image.png"))
            .build();

    Product updatedProduct = Product.builder()
            .id(1L)
            .name("Updated Product")
            .price(new BigDecimal("15.00"))
            .quantity(2)
            .imageUrls(List.of("src/main/resources/image.png"))
            .build();

    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

    when(modelMapper.map(productCreationRequestDto.getProductRequestDto(), Accessory.class)).thenReturn(new Accessory()); // Assuming the type is Accessory for this example

    ResponseEntity<ProductResponseDto> response = productService.update(productCreationRequestDto, 1L);

    // Assert
    assertEquals(200, response.getStatusCodeValue());

    verify(productRepository, times(1)).findById(1L);
    verify(modelMapper, times(1)).map(productCreationRequestDto.getProductRequestDto(), Accessory.class);
  }

  @Test
  void testUpdateProduct_ProductNotFound() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    ProductNotFoundException thrownException = assertThrows(ProductNotFoundException.class, () -> {
      productService.update(productCreationRequestDto, 1L);
    });

    assertEquals("Product with ID: 1 not found", thrownException.getMessage());

    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(0)).save(any(Product.class));
    verify(modelMapper, times(0)).map(any(), any());
  }
  @Test
  void testDeleteProduct_Success() {
    // Arrange
    Product productToDelete = Product.builder()
            .id(1L)
            .name("Product to delete")
            .price(new BigDecimal("10.00"))
            .quantity(1)
            .imageUrls(List.of("src/main/resources/image.png"))
            .build();

    when(productRepository.findById(1L)).thenReturn(Optional.of(productToDelete));

    // Act
    productService.delete(1L);

    // Assert
    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(1)).delete(productToDelete);
  }

  @Test
  void testDeleteProduct_ProductNotFound() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    ProductNotFoundException thrownException = assertThrows(ProductNotFoundException.class, () -> {
      productService.delete(1L);
    });

    assertEquals("Product with ID: 1 not found", thrownException.getMessage());

    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(0)).delete(any(Product.class));
  }
}