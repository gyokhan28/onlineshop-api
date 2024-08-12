package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Repository.ProductRepository;
import com.example.online_shop_api.Static.ProductCategory;
import com.example.online_shop_api.Utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final MinioService minioService;
    private final ValidationUtil validationUtil;

    public ResponseEntity<?> getProduct(Long id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException());

        ProductResponseDto productResponseDto = modelMapper.map(product, ProductResponseDto.class);
        productResponseDto.setImageUrls(minioService.listFilesInDirectoryFullPath(product.getId().toString()));

        return ResponseEntity.ok(productResponseDto);
    }

    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> productResponseDtoList = productRepository.findAll().stream().map(product -> {
                    ProductResponseDto productResponseDto = modelMapper.map(product, ProductResponseDto.class);
                    try {
                        productResponseDto.setImageUrls(minioService.listFilesInDirectoryFullPath(product.getId().toString()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return productResponseDto;
                })
                .toList();
        return ResponseEntity.ok(productResponseDtoList);
    }

    public ResponseEntity<?> addNewProduct(String productType, ProductRequestDto productRequestDto) {
        if (!isValidProductType(productType)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product type not found");
        }

        try {
            // Determine the DTO class name and the entity class name based on the productType
            String dtoClassName = "com.example.online_shop_api.Dto.Request." + productType + "RequestDto";
            String entityClassName = "com.example.online_shop_api.Entity.Products." + productType;

            Class<?> dtoClass = Class.forName(dtoClassName);        // FoodRequestDto
            Class<?> entityClass = Class.forName(entityClassName);  // Food

            Object specificDto = modelMapper.map(productRequestDto, dtoClass);

            Map<String, String> validationErrors = validationUtil.validate(specificDto);
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
            }

            Object product = modelMapper.map(specificDto, entityClass);

            productRepository.save((Product) product);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    private boolean isValidProductType(String productType) {
        return Arrays.stream(ProductCategory.values())
                .map(Enum::name)
                .anyMatch(name -> name.equalsIgnoreCase(productType.toUpperCase()));
    }

}
