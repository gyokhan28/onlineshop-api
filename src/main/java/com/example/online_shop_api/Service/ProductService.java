package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Request.UpdateProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Dto.Response.SuccessResponse;
import com.example.online_shop_api.Entity.Order;
import com.example.online_shop_api.Entity.OrderStatus;
import com.example.online_shop_api.Entity.Products.*;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.ServerErrorException;
import com.example.online_shop_api.Repository.*;
import com.example.online_shop_api.Static.OrderStatusType;
import com.example.online_shop_api.Static.ProductCategory;
import com.example.online_shop_api.Utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final MinioService minioService;
    private final ValidationUtil validationUtil;
    private final Validator validator;

    private void collectRequiredFields(Class<?> clazz, Map<String, String> attributes) {
        if (clazz == null || clazz.equals(Object.class)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (hasValidationAnnotation(field)) {
                attributes.put(field.getName(), field.getType().getSimpleName());
            }
        }

        collectRequiredFields(clazz.getSuperclass(), attributes);
    }

    private boolean hasValidationAnnotation(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof NotNull ||
                    annotation instanceof Size ||
                    annotation instanceof DecimalMin ||
                    annotation instanceof Min) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<?> getProductAttributes(String productType) throws ClassNotFoundException {
        if (!isValidProductType(productType)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product type not found");
        }

        String dtoClassName = "com.example.online_shop_api.Dto.Request." + productType + "RequestDto";
        Class<?> dtoClass = Class.forName(dtoClassName);

        Map<String, String> attributes = new HashMap<>();

        collectRequiredFields(dtoClass, attributes);

        return ResponseEntity.ok(attributes);
    }

    private boolean isValidProductType(String productType) {
        return Arrays.stream(ProductCategory.values())
                .map(Enum::name)
                .anyMatch(name -> name.equalsIgnoreCase(productType.toUpperCase()));
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

    public String validateProduct(ProductRequestDto productRequestDto) {
        Set<ConstraintViolation<ProductRequestDto>> violations = validator.validate(productRequestDto);
        if (violations.isEmpty()) {
            return null;
        }

        return violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
    }

    public ResponseEntity<?> updateProduct(UpdateProductRequestDto updateProductRequestDto, Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with id " + id + " not found");
        }
        Product existingProduct = optionalProduct.get();
        try {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            modelMapper.map(updateProductRequestDto, existingProduct);

            ProductRequestDto tempDto = new ProductRequestDto();
            modelMapper.map(existingProduct, tempDto);

            String validationErrors = validateProduct(tempDto);
            if (validationErrors != null) {
                return ResponseEntity.badRequest().body("Validation errors: " + validationErrors);
            }
            productRepository.save(existingProduct);
            return ResponseEntity.ok("Product updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    public ResponseEntity<?> deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with id " + id + " not found");
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok(new SuccessResponse("Product deleted successfully"));
    }
}
