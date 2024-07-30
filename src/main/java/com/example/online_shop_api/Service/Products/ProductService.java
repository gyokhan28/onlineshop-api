package com.example.online_shop_api.Service.Products;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.*;
import com.example.online_shop_api.Exceptions.ProductNotFoundException;
import com.example.online_shop_api.Repository.BrandRepository;
import com.example.online_shop_api.Repository.ColorRepository;
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
  private final ColorRepository colorRepository;
  private final BrandRepository brandRepository;

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

  public ResponseEntity<ProductResponseDto> create(ProductRequestDto request) {
    Product product = validateProductType(modelMapper.map(request, Product.class));

    product.setImageUrls(List.of(request.getImageLocation()));

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

  private Product validateProductType(Product product) {
    if (product instanceof Accessory) {
      return modelMapper.map(product, Accessory.class);
    } else if (product instanceof Decoration) {
      return modelMapper.map(product, Decoration.class);
    } else if (product instanceof Drink) {
      return modelMapper.map(product, Drink.class);
    } else if (product instanceof Food) {
      return modelMapper.map(product, Food.class);
    } else if (product instanceof Others) {
      return modelMapper.map(product, Others.class);
    } else if (product instanceof Railing) {
      return modelMapper.map(product, Railing.class);
    } else if (product instanceof Sanitary) {
      return modelMapper.map(product, Sanitary.class);
    }
    return new Product();
  }
}
