package com.example.online_shop_api.Service.Products;

import com.example.online_shop_api.Dto.Request.ProductCreationRequestDto;
import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.*;
import com.example.online_shop_api.Exceptions.ProductNotFoundException;
import com.example.online_shop_api.Repository.Products.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ModelMapper modelMapper;

  public ResponseEntity<List<ProductResponseDto>> getAllAccessories() {
    List<Product> productList = productRepository.findAll();
    return ResponseEntity.ok(
        productList.stream()
            .map(product -> modelMapper.map(product, ProductResponseDto.class))
            .toList());
  }

  public ProductResponseDto getById(Long id) {
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    return modelMapper.map(product, ProductResponseDto.class);
  }

  public ResponseEntity<ProductResponseDto> create(ProductCreationRequestDto request) {
    Product product = validateProductType(request);

    product.setImageUrls(List.of(request.getProductRequestDto().getImageLocation()));

    productRepository.save(product);
    return ResponseEntity.ok(modelMapper.map(product, ProductResponseDto.class));
  }

  public ResponseEntity<ProductResponseDto> update(ProductRequestDto request, Long id) {

    Product existingProduct =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    modelMapper.map(request, existingProduct);
    existingProduct.setImageUrls(List.of(request.getImageLocation()));

    Product updatedProduct = productRepository.save(existingProduct);

    ProductResponseDto responseDto = modelMapper.map(updatedProduct, ProductResponseDto.class);
    return ResponseEntity.ok(responseDto);
  }

  public void deleteAccessory(Long id) {
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    productRepository.delete(product);
  }

  public Product validateProductType(ProductCreationRequestDto request) {
    String productType = request.getProductType();
    ProductRequestDto productRequestDto = request.getProductRequestDto();
    return switch (productType) {
      case "Accessory" -> modelMapper.map(productRequestDto, Accessory.class);
      case "Decoration" -> modelMapper.map(productRequestDto, Decoration.class);
      case "Drink" -> modelMapper.map(productRequestDto, Drink.class);
      case "Food" -> modelMapper.map(productRequestDto, Food.class);
      case "Others" -> modelMapper.map(productRequestDto, Others.class);
      case "Railing" -> modelMapper.map(productRequestDto, Railing.class);
      case "Sanitary" -> modelMapper.map(productRequestDto, Sanitary.class);
      default -> throw new IllegalArgumentException("Invalid product type: " + productType);
    };
  }
}
