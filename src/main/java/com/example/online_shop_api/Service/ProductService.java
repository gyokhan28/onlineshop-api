package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.AddProductRequest;
import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Entity.Order;
import com.example.online_shop_api.Entity.OrderStatus;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.ServerErrorException;
import com.example.online_shop_api.Repository.BrandRepository;
import com.example.online_shop_api.Repository.ColorRepository;
import com.example.online_shop_api.Repository.MaterialRepository;
import com.example.online_shop_api.Repository.OrderRepository;
import com.example.online_shop_api.Static.OrderStatusType;
import com.example.online_shop_api.Static.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.time.chrono.JapaneseEra.values;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final MaterialRepository materialRepository;
    private final ColorRepository colorRepository;
    private final BrandRepository brandRepository;
    private final OrderRepository orderRepository;
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
}
