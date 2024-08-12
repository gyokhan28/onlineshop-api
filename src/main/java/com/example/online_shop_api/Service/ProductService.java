package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.AddProductRequest;
import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Order;
import com.example.online_shop_api.Entity.OrderStatus;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.ServerErrorException;
import com.example.online_shop_api.Repository.*;
import com.example.online_shop_api.Static.OrderStatusType;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final MaterialRepository materialRepository;
    private final ColorRepository colorRepository;
    private final BrandRepository brandRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final MinioService minioService;
    private final ValidationUtil validationUtil;
    public ResponseEntity<?> addNewProduct(String productType) {
        if (!isValidProductType(productType)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product type not found");
        }

        AddProductRequest request = new AddProductRequest();
        ProductRequestDto productRequestDto = new ProductRequestDto();

        request.setProductRequestDto(productRequestDto);
        request.setProductType(productType);
        addAttributesDependingOnProductType(productType, request);

        return ResponseEntity.ok(request);
    }

    private boolean isValidProductType(String productType) {
        return Arrays.stream(ProductCategory.values())
                .map(Enum::name)
                .anyMatch(name -> name.equalsIgnoreCase(productType.toUpperCase()));
    }

    private void addAttributesDependingOnProductType(String productType, AddProductRequest response) {
        if (productType.equalsIgnoreCase("Sanitary") || productType.equalsIgnoreCase("Railing") || productType.equalsIgnoreCase("Decoration") || productType.equalsIgnoreCase("Others")) {
            response.setMaterials(materialRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Railing") || productType.equalsIgnoreCase("Accessories")) {
            response.setColors(colorRepository.findAll());
            response.setBrands(brandRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Decoration")) {
            response.setBrands(brandRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Others")) {
            response.setColors(colorRepository.findAll());
        }
    }

    private List<Order> getUserOrdersByOrderStatus(User user, OrderStatus orderStatus) {
        return orderRepository.findAllByUser_IdAndStatus_Id(user.getId(), orderStatus.getId());
    }
    public Optional<Order> getBasketOrder(User user) {
        OrderStatus basketOrderStatus = OrderStatus.builder()
                .id(OrderStatusType.BASKET.getId())
                .name(OrderStatusType.BASKET.name())
                .build();

        List<Order> basketOrders = getUserOrdersByOrderStatus(user, basketOrderStatus);

        if (basketOrders.size() > 1) {
            throw new ServerErrorException("Critical server error. More than one basket for user with userID: " + user.getId());
        }

        if (basketOrders.size() == 1) {
            return Optional.ofNullable(basketOrders.get(0));
        }

        return Optional.empty();
    }

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
}
