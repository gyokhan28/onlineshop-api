package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Dto.Response.*;
import com.example.online_shop_api.Entity.*;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Exceptions.*;
import com.example.online_shop_api.Mapper.OrderMapper;
import com.example.online_shop_api.Mapper.ProductMapper;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.Repository.*;
import com.example.online_shop_api.Static.OrderStatusType;
import com.example.online_shop_api.Static.RoleType;
import com.example.online_shop_api.Utils.ValidationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder encoder;
    private final CityRepository cityRepository;
    private final ValidationUtil validationUtil;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderStatusRepository orderStatusRepository;

    private boolean isEmailInDB(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isPhoneNumberInDB(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    private boolean isUsernameInDB(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean arePasswordsMatching(UserRequestDto userRequestDto) {
        return userRequestDto.getPassword().equals(userRequestDto.getRepeatedPassword());
    }

    private boolean isValidCityId(Long cityId) {
        return cityRepository.findById(cityId).isPresent();
    }

    public void validateNewUser(UserRequestDto userRequestDto) {
        if (isEmailInDB(userRequestDto.getEmail())) {
            throw new EmailInUseException("Email already in use. Please use a different email!");
        }
        if (isUsernameInDB(userRequestDto.getUsername())) {
            throw new UsernameInUseException("Username already in use. Please use a different username!");
        }
        if (!arePasswordsMatching(userRequestDto)) {
            throw new PasswordsNotMatchingException("Passwords don't match");
        }
        if (!isValidCityId(userRequestDto.getCityId())) {
            throw new CityNotFoundException("City doesn't exist");
        }
    }

    public String addNewUser(UserRequestDto userRequestDto) {
        Role userRole = new Role(RoleType.ROLE_USER.getId(), RoleType.ROLE_USER.name());
        try {
            User user = UserMapper.toEntity(userRequestDto);
            addressRepository.save(user.getAddress());
            user.setRole(userRole);
            user.setPassword(encoder.encode(userRequestDto.getPassword()));
            userRepository.save(user);
            return "Account created successfully!";
        } catch (Exception exception) {
            throw new ServerErrorException("An internal error occurred. Please try again.");
        }
    }

    public ResponseEntity<?> registerNewUser(@Valid @RequestBody UserRequestDto userRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        Map<String, String> validationErrors = validationUtil.validate(userRequestDto);
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(validationErrors);
        }
        try {
            validateNewUser(userRequestDto);
        } catch (EmailInUseException | UsernameInUseException | PasswordsNotMatchingException |
                 CityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(addNewUser(userRequestDto));
    }

    public ResponseEntity<UserProfileResponse> viewProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        UserProfileResponse response = new UserProfileResponse();
        response.setUserResponseDto(UserMapper.toDto(user));
        List<OrderResponseDto> orderResponseDtoList = getUserOrders(user);
        response.setOrderList(orderResponseDtoList);
        return ResponseEntity.ok(response);
    }

    private List<OrderResponseDto> getUserOrders(User user) {
        List<Order> currentUserOrders = orderRepository.findOrdersByUserIdAndStatusNotBasket(user.getId());
        List<OrderResponseDto> userOrders = new ArrayList<>();
        for (Order o : currentUserOrders) {
            OrderResponseDto order = OrderMapper.toDto(o);
            List<OrderProduct> orderProducts = orderProductRepository.findAllByOrder_Id(o.getId());
            BigDecimal price = calculateTotalPrice(orderProducts);
            order.setPrice(price);
            userOrders.add(order);
        }
        return userOrders;
    }

    private List<BasketProductResponseDTO> getBasketProducts(Order basketOrder) throws Exception {
        List<OrderProduct> products = orderProductRepository.findAllByOrderId(basketOrder.getId());
        List<BasketProductResponseDTO> responseList = new ArrayList<>();
        for (OrderProduct op : products) {
            BasketProductResponseDTO responseProduct = ProductMapper.toDto(op.getProduct());

            BigDecimal price = op.getProduct().getPrice();
            BigDecimal quantity = BigDecimal.valueOf(op.getQuantity());
            BigDecimal subTotal = price.multiply(quantity);

            responseProduct.setId(op.getProduct().getId());
            responseProduct.setSubtotal(subTotal);
            responseProduct.setQuantity(op.getQuantity());
            responseList.add(responseProduct);
        }
        return responseList;
    }

    private BigDecimal calculateTotalPrice(List<OrderProduct> orderProducts) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderProduct product : orderProducts) {
            total = total.add(product.getProductPriceWhenPurchased().multiply(BigDecimal.valueOf(product.getQuantity())));
        }
        return total;
    }

    public ResponseEntity<BasketResponse> getBasket(Authentication authentication) throws Exception {
        User user = getCurrentUser(authentication);
        BasketResponse basketResponse = new BasketResponse(new ArrayList<>(), BigDecimal.ZERO);

        Optional<Order> basketOrder = productService.getBasketOrder(user);
        if (basketOrder.isPresent()) {
            List<BasketProductResponseDTO> basketProducts = getBasketProducts(basketOrder.get());
            List<OrderProduct> orderProducts = orderProductRepository.findAllByOrder_Id(basketOrder.get().getId());
            BigDecimal totalPrice = calculateTotalPrice(orderProducts);
            basketResponse.setProducts(basketProducts);
            basketResponse.setTotalPrice(totalPrice);
        }

        return ResponseEntity.ok(basketResponse);
    }

    private List<InsufficientQuantityResponse> validateProductAvailability(List<OrderProduct> orderProducts) {
        List<InsufficientQuantityResponse> errors = new ArrayList<>();
        for (OrderProduct op : orderProducts) {
            Optional<Product> optionalProduct = productRepository.findByIdNotDeleted(op.getProduct().getId());
            if (optionalProduct.isPresent()) {
                Product productInStock = optionalProduct.get();
                if (productInStock.getQuantity() < op.getQuantity()) {
                    errors.add(new InsufficientQuantityResponse(op.getProduct().getName(),
                            op.getQuantity(),
                            productInStock.getQuantity()));
                }
            }
        }
        return errors;
    }

    public ResponseEntity<?> buyNow(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        User user = optionalUser.get();

        Optional<Order> optionalBasketOrder = productService.getBasketOrder(user);
        if (optionalBasketOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("The basket of user " + user.getId() + " is empty!");
        }
        Order basketOrder = optionalBasketOrder.get();
        Long orderId = basketOrder.getId();
        List<OrderProduct> orderProductList = orderProductRepository.findAllByOrder_Id(orderId);
        BigDecimal totalPrice = calculateTotalPrice(orderProductList);

        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrder_Id(orderId);
        List<InsufficientQuantityResponse> errors = validateProductAvailability(orderProducts);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new BuyNowResponse(false, errors, totalPrice, orderId));
        }

        processOrder(orderProducts, basketOrder);

        return ResponseEntity.ok(new BuyNowResponse(true, null, totalPrice, orderId));
    }

    private void processOrder(List<OrderProduct> orderProducts, Order basketOrder) {
        reduceStockQuantity(orderProducts);
        saveThePurchasePriceInOrderProduct(orderProducts);
        OrderStatus pending = new OrderStatus(OrderStatusType.PENDING.getId(), OrderStatusType.PENDING.name());
        basketOrder.setStatus(pending);
        orderRepository.save(basketOrder);
    }

    private void reduceStockQuantity(List<OrderProduct> orderProducts) {
        for (OrderProduct op : orderProducts) {
            Optional<Product> optionalProduct = productRepository.findByIdNotDeleted(op.getProduct().getId());
            if (optionalProduct.isPresent()) {
                Product productInStock = optionalProduct.get();
                productInStock.setQuantity(productInStock.getQuantity() - op.getQuantity());
                productRepository.save(productInStock);
            }
        }
    }

    private void saveThePurchasePriceInOrderProduct(List<OrderProduct> orderProducts) {
        for (OrderProduct op : orderProducts) {
            Optional<Product> optionalProduct = productRepository.findByIdNotDeleted(op.getProduct().getId());
            if (optionalProduct.isPresent()) {
                Product productInStock = optionalProduct.get();
                op.setProductPriceWhenPurchased(productInStock.getPrice());
                orderProductRepository.save(op);
            }
        }
    }

    private void updateProductQuantity(Order order, Long productId, int newQuantity) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product with id " + productId + " not found!");
        }
        if (newQuantity < 0) {
            throw new QuantityNotAvailableException("The quantity cannot be negative number");
        }
        Product product = optionalProduct.get();
        if (!isQuantityAvailable(product, newQuantity)) {
            throw new QuantityNotAvailableException("The quantity that you are trying to set is not available!");
        }
        List<OrderProduct> currentOrderProducts = orderProductRepository.findAllByOrder(order);
        Long currentOrderProductId;
        try {
            currentOrderProductId = orderProductRepository.findByOrderIdAndProductId(order.getId(), productId).getId();
        } catch (RuntimeException e) {
            throw new RuntimeException("Order/Product was not found!");
        }
        if (newQuantity == 0) {
            orderProductRepository.deleteById(currentOrderProductId);
            return;
        }
        for (OrderProduct op : currentOrderProducts) {
            if (op.getProduct().getId().equals(productId)) {
                op.setQuantity(newQuantity);
                orderProductRepository.save(op);
                return;
            }
        }
    }

    private boolean isQuantityAvailable(Product product, int newQuantity) {
        return product.getQuantity() >= newQuantity;
    }

    public ResponseEntity<?> updateQuantity(Long productId, int quantity, Authentication authentication) throws Exception {
        User user = getCurrentUser(authentication);
        Optional<Order> optionalBasketOrder = productService.getBasketOrder(user);
        if (optionalBasketOrder.isEmpty()) {
            return ResponseEntity.badRequest().body("Order not found!");
        }
        Order basketOrder = optionalBasketOrder.get();
        Long orderId = basketOrder.getId();
        try {
            updateProductQuantity(basketOrder, productId, quantity);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        List<OrderProduct> orderProductList = orderProductRepository.findAllByOrder_Id(orderId);
        BigDecimal totalPrice = calculateTotalPrice(orderProductList);
        List<BasketProductResponseDTO> basketProducts = getBasketProducts(basketOrder);
        return ResponseEntity.ok(new BasketResponse(basketProducts, totalPrice));

    }

    private String formatDeliveryAddress(User user) {
        return user.getAddress().getCity().getName() + ", " + user.getAddress().getStreetName();
    }

    private List<ProductResponse> fillProductResponse(List<OrderProduct> products) {
        List<ProductResponse> productResponses = new ArrayList<>();
        for (OrderProduct op : products) {
            ProductResponse productResponse = new ProductResponse(op.getProduct().getName(), op.getQuantity());
            productResponses.add(productResponse);
        }
        return productResponses;
    }

    private UserOrdersResponse createUserOrderResponse(User user, Order order) {
        UserOrdersResponse response = new UserOrdersResponse();
        response.setOrderId(order.getId());
        response.setDeliveryAddress(formatDeliveryAddress(user));
        response.setOrderDate(order.getOrderDateTime());
        response.setStatus(order.getStatus().getName());

        List<OrderProduct> products = orderProductRepository.findAllByOrder_Id(order.getId());
        List<ProductResponse> productResponses = fillProductResponse(products);

        List<OrderProduct> basketProducts = orderProductRepository.findAllByOrder_Id(order.getId());
        BigDecimal price = calculateTotalPrice(basketProducts);

        response.setPrice(price);
        response.setProducts(productResponses);

        return response;
    }

    public ResponseEntity<List<UserOrdersResponse>> getCurrentUserOrders(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user;
        if (optionalUser.isEmpty()) {
            return null;
        }
        user = optionalUser.get();
        List<Order> currentUSerOrders = orderRepository.findOrdersByUserIdAndStatusNotBasket(user.getId());
        List<UserOrdersResponse> responseList = new ArrayList<>();

        for (Order o : currentUSerOrders) {
            UserOrdersResponse response = createUserOrderResponse(user, o);
            responseList.add(response);
        }
        return ResponseEntity.ok(responseList);
    }

    private void checkIfOrderHasStatusBasket(Order order) {
        Optional<OrderStatus> optionalBasketStatus = orderStatusRepository.findByName("BASKET");
        if (optionalBasketStatus.isPresent()) {
            OrderStatus basketStatus = optionalBasketStatus.get();
            if (order.getStatus().equals(basketStatus)) {
                throw new OrderStatusException("Orders with status \"Basket\" cannot be cancelled!");
            }
        }
    }

    private void checkIfOrderHasStatusCancelled(Order order) {
        Optional<OrderStatus> optionalCancelledStatus = orderStatusRepository.findByName("CANCELLED");
        if (optionalCancelledStatus.isPresent()) {
            OrderStatus cancelledStatus = optionalCancelledStatus.get();
            if (order.getStatus().equals(cancelledStatus)) {
                throw new OrderStatusException("This order is already cancelled!");
            }
        }
    }

    private boolean isOrderOfCurrentUser(Order order, User user) {
        return order.getUser().getId().equals(user.getId());
    }

    public ResponseEntity<String> cancelOrder(Long orderId, Authentication authentication) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Optional<OrderStatus> optionalStatus = orderStatusRepository.findByName("CANCELLED");
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.badRequest().body("Order not found!");
        }
        if (optionalStatus.isEmpty()) {
            return ResponseEntity.badRequest().body("Status not found!");
        }
        Order order = optionalOrder.get();
        if (!isOrderOfCurrentUser(order, getCurrentUser(authentication))) {
            return ResponseEntity.badRequest().body("This order is not made by the logged user");
        }
        try {
            checkIfOrderHasStatusBasket(order);
            checkIfOrderHasStatusCancelled(order);
        } catch (OrderStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        OrderStatus cancelledStatus = optionalStatus.get();
        order.setStatus(cancelledStatus);
        order.setOrderCancelDateTime(LocalDateTime.now());
        returnProductsToStock(orderId);
        orderRepository.save(order);
        return ResponseEntity.ok("Order cancelled successfully!");
    }

    private void returnProductsToStock(Long orderId) {
        List<OrderProduct> orderProductsList = orderProductRepository.findAllByOrderId(orderId);
        for (OrderProduct op : orderProductsList) {
            Optional<Product> optionalProduct = productRepository.findById(op.getProduct().getId());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.setQuantity(product.getQuantity() + op.getQuantity());
                productRepository.save(product);
            }
        }
    }

    //Get page with user profile having all data that can be changed (fName, lName, email, Address, phoneNumber)
    //The user edits the data (fields will be filled with the current user data)
    public ResponseEntity<UserEditResponse> getCurrentUserToRequest(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    private User getCurrentUser(Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        return myUserDetails.getUser();
    }

    public ResponseEntity<?> editUserProfile(BindingResult bindingResult, @RequestBody @Valid UserEditResponse userEditResponse, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        User user = getCurrentUser(authentication);
        try {
            updateUserFields(user, userEditResponse);
        } catch (EmailInUseException | PhoneInUseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        userRepository.save(user);
        return ResponseEntity.ok(new SuccessResponse("Profile updated successfully"));
    }

    private void updateUserFields(User user, UserEditResponse userEditResponse) {
        updateFirstName(user, userEditResponse.getFirstName());
        updateLastName(user, userEditResponse.getLastName());
        updateEmail(user, userEditResponse.getEmail());
        updateAddress(user, userEditResponse.getCity(), userEditResponse.getStreetName());
        updatePhoneNumber(user, userEditResponse.getPhoneNumber());
    }

    private void updateFirstName(User user, String firstName) {
        if (firstName != null) {
            user.setFirstName(firstName);
        }
    }

    private void updateLastName(User user, String lastName) {
        if (lastName != null) {
            user.setLastName(lastName);
        }
    }

    private void updateEmail(User user, String email) {
        if (user.getEmail().equalsIgnoreCase(email)) {
            return;
        }
        if (email != null) {
            if (!isEmailInDB(email)) {
                user.setEmail(email);
            } else {
                throw new EmailInUseException("Email already in use!");
            }
        }
    }

    private void updateAddress(User user, String city, String streetName) {
        if (city != null || streetName != null) {
            Address address = user.getAddress();
            if (address == null) {
                address = new Address();
                user.setAddress(address);
            }
            if (city != null) {
                String cityName = formatCityName(city);
                City cityEntity = cityRepository.findByName(cityName);
                address.setCity(cityEntity);
            }
            if (streetName != null) {
                address.setStreetName(streetName);
            }
        }
    }

    private void updatePhoneNumber(User user, String phoneNumber) {
        if (user.getPhoneNumber().equalsIgnoreCase(phoneNumber)) {
            return;
        }
        if (phoneNumber != null) {
            if (!isPhoneNumberInDB(phoneNumber)) {
                user.setPhoneNumber(phoneNumber);
            } else {
                throw new PhoneInUseException("Phone number already in use!");
            }
        }
    }

    //SOFIA/sofia -> Sofia
    public String formatCityName(String cityName) {
        return cityName.substring(0, 1).toUpperCase() + cityName.substring(1).toLowerCase();
    }
}

