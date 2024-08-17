package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Dto.Response.*;
import com.example.online_shop_api.Entity.*;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Exceptions.*;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.Repository.*;
import com.example.online_shop_api.Service.MinioService;
import com.example.online_shop_api.Service.ProductService;
import com.example.online_shop_api.Service.UserService;
import com.example.online_shop_api.Static.OrderStatusType;
import com.example.online_shop_api.Utils.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserServiceTests {
    private UserRequestDto userRequestDto;

    @Mock
    UserRepository userRepository;
    @Mock
    CityRepository cityRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    OrderStatusRepository orderStatusRepository;
    @Mock
    Authentication authentication;
    @Spy
    @InjectMocks
    private UserService userService;
    @Mock
    BCryptPasswordEncoder encoder;
    @Mock
    ValidationUtil validationUtil;
    @Mock
    private MockedStatic<UserMapper> mockedStaticUserMapper;
    @Mock
    private ProductService productService;
    @Mock
    private MinioService minioService;
    @Mock
    private User user;
    @Mock
    private MyUserDetails myUserDetails;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private OrderProductRepository orderProductRepository;

    @BeforeEach
    public void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("testFirstName");
        userRequestDto.setLastName("testLastName");
        userRequestDto.setUsername("testUserName");
        userRequestDto.setPassword("password123");
        userRequestDto.setRepeatedPassword("password123");
        userRequestDto.setEmail("testMail");
        userRequestDto.setCityId(1L);
    }

    @Test
    void testValidateNewUser_EmailInUse() {
        userRequestDto.setEmail("test@mail.bg");
        when(userRepository.findByEmail("test@mail.bg")).thenReturn(Optional.of(new User()));

        assertThrows(EmailInUseException.class, () -> userService.validateNewUser(userRequestDto));
    }

    @Test
    void testValidateNewUser_UsernameInUse() {
        userRequestDto.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        assertThrows(UsernameInUseException.class, () -> userService.validateNewUser(userRequestDto));
    }

    @Test
    void testValidateNewUser_PasswordsNotMatching() {
        userRequestDto.setPassword("test123");
        userRequestDto.setRepeatedPassword("test321");

        assertThrows(PasswordsNotMatchingException.class, () -> userService.validateNewUser(userRequestDto));
    }

    @Test
    void testAddNewUser_CorrectParameters() {
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        User user = new User();
        Address address = new Address();
        user.setAddress(address);

        mockedStaticUserMapper.when(() -> UserMapper.toEntity(Mockito.any(UserRequestDto.class))).thenReturn(user);

        when(encoder.encode("pass123")).thenReturn("encodedPassword");

        String actualResult = userService.addNewUser(userRequestDto);

        assertEquals("Account created successfully!", actualResult);
        verify(addressRepository, times(1)).save(user.getAddress());
        verify(userRepository, times(1)).save(user);

        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void testAddNewUser_ThrowException() {
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        mockedStaticUserMapper.when(() -> UserMapper.toEntity(Mockito.any(UserRequestDto.class))).thenThrow(new RuntimeException("Error"));

        assertThrows(ServerErrorException.class, () -> userService.addNewUser(userRequestDto));
    }

    @Test
    void testRegisterNewUser_Success() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        userRequestDto.setEmail("test@mail.bg");
        userRequestDto.setUsername("testuser");
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        userRequestDto.setCityId(1L);

        User user = new User();
        user.setAddress(new Address());

        mockedStaticUserMapper.when(() -> UserMapper.toEntity(Mockito.any(UserRequestDto.class))).thenReturn(user);
        when(encoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");
        when(cityRepository.findById(1L)).thenReturn(Optional.of(new City()));

        ResponseEntity<?> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.ok("Account created successfully!"), response);

        verify(addressRepository, times(1)).save(user.getAddress());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testValidateNewUser_ValidEmailAndUsername() {
        userRequestDto.setEmail("unique@mail.bg");
        userRequestDto.setUsername("uniqueuser");
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        userRequestDto.setCityId(1L);

        when(userRepository.findByEmail("unique@mail.bg")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("uniqueuser")).thenReturn(Optional.empty());
        when(cityRepository.findById(1L)).thenReturn(Optional.of(new City()));

        assertDoesNotThrow(() -> userService.validateNewUser(userRequestDto));
    }

    @Test
    void testAddNewUser_UserMapperThrowsException() {
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        mockedStaticUserMapper.when(() -> UserMapper.toEntity(Mockito.any(UserRequestDto.class))).thenThrow(new RuntimeException("Mapping error"));

        assertThrows(ServerErrorException.class, () -> userService.addNewUser(userRequestDto));
    }

    @Test
    void testRegisterNewUser_ThrowsEmailInUseException() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.badRequest().body("Email already in use. Please use a different email!"), response);
    }

    @Test
    void testRegisterNewUser_ThrowsUsernameInUseException() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.badRequest().body("Username already in use. Please use a different username!"), response);
    }

    @Test
    void testRegisterNewUser_ThrowsPasswordsNotMatchingException() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        userRequestDto.setRepeatedPassword("password321");

        assertThrows(PasswordsNotMatchingException.class, () -> userService.validateNewUser(userRequestDto));

        ResponseEntity<?> actualResponse = userService.registerNewUser(userRequestDto, bindingResult);
        ResponseEntity<String> expectedResponse = ResponseEntity.badRequest().body("Passwords don't match");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testRegisterNewUser_BindingResultErrors() {
        UserRequestDto userRequestDto = new UserRequestDto();

        ObjectError error1 = new ObjectError("field1", "Field1 error message");
        ObjectError error2 = new ObjectError("field2", "Field2 error message");
        List<ObjectError> errors = Arrays.asList(error1, error2);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        ResponseEntity<?> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.badRequest().body(errors.toString()), response);
    }

    @Test
    void testAddNewUser_ThrowsServerErrorException() {
        userRequestDto.setEmail("test@mail.bg");
        userRequestDto.setUsername("testuser");
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");

        mockedStaticUserMapper.when(() -> UserMapper.toEntity(Mockito.any(UserRequestDto.class))).thenThrow(new RuntimeException("Mapping error"));

        ServerErrorException thrownException = assertThrows(ServerErrorException.class, () -> {
            userService.addNewUser(userRequestDto);
        });

        assertEquals("An internal error occurred. Please try again.", thrownException.getMessage());
    }

    @Test
    void testAddNewUser_ThrowsCityNotFoundException() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        userRequestDto.setEmail("test@mail.bg");
        userRequestDto.setUsername("testuser");
        userRequestDto.setPassword("password123");
        userRequestDto.setRepeatedPassword("password123");

        assertThrows(CityNotFoundException.class, () -> userService.validateNewUser(userRequestDto));

        ResponseEntity<?> actualResponse = userService.registerNewUser(userRequestDto, bindingResult);
        ResponseEntity<String> expectedResponse = ResponseEntity.badRequest().body("City doesn't exist");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testAddNewUser_UserSaveFails() {
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        User user = new User();
        mockedStaticUserMapper.when(() -> UserMapper.toEntity(Mockito.any(UserRequestDto.class))).thenReturn(user);
        when(encoder.encode("pass123")).thenReturn("encodedPassword");
        doThrow(new RuntimeException("User save error")).when(userRepository).save(user);

        assertThrows(ServerErrorException.class, () -> userService.addNewUser(userRequestDto));
    }

    @Test
    void testRegisterNewUser_ValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Map<String, String> errors = new HashMap<>();
        errors.put("firstName", "firstName was not entered");
        when(validationUtil.validate(userRequestDto)).thenReturn(errors);

        ResponseEntity<?> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.badRequest().body(errors), response);
    }

    @Test
    void testAddNewUser_AddressSaveFails() {
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        User user = new User();
        lenient().doThrow(new RuntimeException("Address save error")).when(addressRepository).save(user.getAddress());

        assertThrows(ServerErrorException.class, () -> userService.addNewUser(userRequestDto));
    }

    @Test
    void testViewProfile_Success() {
        Authentication authentication = mock(Authentication.class);
        MyUserDetails userDetails = mock(MyUserDetails.class);
        User user = new User();
        user.setId(1L);
        Address address = new Address();
        user.setAddress(address);
        user.setUsername("testUser");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);
        mockedStaticUserMapper.when(() -> UserMapper.toDto(Mockito.any(User.class))).thenReturn(new UserResponseDto());

        ResponseEntity<UserProfileResponse> response = userService.viewProfile(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        mockedStaticUserMapper.verify(() -> UserMapper.toDto(user), times(1));
    }

    @Test
    public void testBuyNowSuccess() {
        Authentication authentication = mock(Authentication.class);
        MyUserDetails userDetails = mock(MyUserDetails.class);

        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.TEN);

        User testUser = User.builder()
                .id(1L)
                .build();

        Order basketOrder = Order.builder()
                .status(OrderStatus.builder().name("BASKET").id(0L).build())
                .user(testUser)
                .id(1L)
                .build();
        when(productService.getBasketOrder(testUser)).thenReturn(Optional.of(basketOrder));

        Product p1 = Product.builder()
                .price(BigDecimal.TEN)
                .quantity(100)
                .build();
        OrderProduct op1 = OrderProduct.builder()
                .order(basketOrder)
                .product(p1)
                .quantity(5)
                .productPriceWhenPurchased(BigDecimal.TEN)
                .build();

        List<OrderProduct> orderProductList = List.of(op1);

        when(orderProductRepository.findAllByOrder_Id(basketOrder.getId())).thenReturn(orderProductList);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(testUser);

        ResponseEntity<?> responseEntity = userService.buyNow(authentication);
        if (responseEntity.getBody() instanceof BuyNowResponse buyNowResponse) {
            assertNotNull(buyNowResponse);
            assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
            assertTrue(buyNowResponse.isSuccess());
            assertNull(buyNowResponse.getErrors());
            assertEquals(BigDecimal.valueOf(50), buyNowResponse.getTotalPrice());
            assertEquals(1L, buyNowResponse.getOrderId());
        } else {
            fail("Expected BuyNowResponse but got " + responseEntity.getBody().getClass().getName());
        }
    }

    @Test
    public void testEditUserProfile_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        User currentUser = new User();
        currentUser.setEmail("existing.email@example.com");
        currentUser.setPhoneNumber("1234567");
        MyUserDetails userDetails = new MyUserDetails(currentUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        UserEditResponse userEditResponse = new UserEditResponse();
        userEditResponse.setFirstName("John");
        userEditResponse.setLastName("Doe");
        userEditResponse.setEmail("john.doe@example.com");
        userEditResponse.setCity("New York");
        userEditResponse.setStreetName("5th Avenue");
        userEditResponse.setPhoneNumber("1234567890");

        ResponseEntity<?> response = userService.editUserProfile(bindingResult, userEditResponse, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        SuccessResponse responseBody = (SuccessResponse) response.getBody();
        assertEquals("Profile updated successfully", responseBody.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testEditUserProfile_EmailInUse() {
        UserEditResponse userEditResponse = new UserEditResponse();
        userEditResponse.setEmail("new@example.com");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getUser()).thenReturn(user);

        when(user.getEmail()).thenReturn("existing@example.com");
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.editUserProfile(bindingResult, userEditResponse, authentication);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already in use!", response.getBody());
    }

    @Test
    public void testEditUserProfile_PhoneNumberInUse() {
        UserEditResponse userEditResponse = new UserEditResponse();
        userEditResponse.setPhoneNumber("1234567890");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getUser()).thenReturn(user);


        when(user.getPhoneNumber()).thenReturn("0987654321");
        when(user.getEmail()).thenReturn("existing@example.com");
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.editUserProfile(bindingResult, userEditResponse, authentication);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Phone number already in use!", response.getBody());
    }

    @Test
    void testGetCurrentUserToRequest() {
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getUser()).thenReturn(user);
        UserEditResponse expectedResponse = new UserEditResponse();
        when(UserMapper.toResponse(user)).thenReturn(expectedResponse);

        ResponseEntity<UserEditResponse> responseEntity = userService.getCurrentUserToRequest(authentication);

        assertEquals(ResponseEntity.ok(expectedResponse), responseEntity);
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        lenient().when(authentication.getPrincipal()).thenReturn(myUserDetails);
        lenient().when(myUserDetails.getUser()).thenReturn(user);

        lenient().when(orderRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ResponseEntity<String> response = userService.cancelOrder(1L, authentication);

        assertEquals(ResponseEntity.badRequest().body("Order not found!"), response);
    }

    @Test
    void testCancelOrder_StatusNotFound() {
        lenient().when(authentication.getPrincipal()).thenReturn(myUserDetails);
        lenient().when(myUserDetails.getUser()).thenReturn(user);
        Order order = new Order();
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByName("CANCELLED")).thenReturn(Optional.empty());

        ResponseEntity<String> response = userService.cancelOrder(1L, authentication);

        assertEquals(ResponseEntity.badRequest().body("Status not found!"), response);
    }

    @Test
    void testCancelOrder_OrderNotOfCurrentUser() {
        lenient().when(authentication.getPrincipal()).thenReturn(myUserDetails);
        lenient().when(myUserDetails.getUser()).thenReturn(user);
        Order order = new Order();
        User otherUser = new User();
        otherUser.setId(2L);
        order.setUser(otherUser);
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByName("CANCELLED")).thenReturn(Optional.of(new OrderStatus()));

        ResponseEntity<String> response = userService.cancelOrder(1L, authentication);

        assertEquals(ResponseEntity.badRequest().body("This order is not made by the logged user"), response);
    }

    @Test
    void testCancelOrder_OrderHasStatusBasket() {
        User currentUser = new User();
        currentUser.setId(1L);
        lenient().when(authentication.getPrincipal()).thenReturn(myUserDetails);
        lenient().when(myUserDetails.getUser()).thenReturn(currentUser);
        Order order = new Order();
        order.setUser(currentUser);
        OrderStatus basketStatus = new OrderStatus();
        basketStatus.setName("BASKET");
        order.setStatus(basketStatus);

        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByName("CANCELLED")).thenReturn(Optional.of(new OrderStatus()));
        when(orderStatusRepository.findByName("BASKET")).thenReturn(Optional.of(basketStatus));

        ResponseEntity<String> response = userService.cancelOrder(1L, authentication);

        assertEquals(ResponseEntity.badRequest().body("Orders with status \"Basket\" cannot be cancelled!"), response);
    }

    @Test
    void testCancelOrder_OrderAlreadyCancelled() {
        User currentUser = new User();
        currentUser.setId(1L);
        lenient().when(authentication.getPrincipal()).thenReturn(myUserDetails);
        lenient().when(myUserDetails.getUser()).thenReturn(currentUser);
        Order order = new Order();
        order.setUser(currentUser);
        OrderStatus cancelledStatus = new OrderStatus();
        cancelledStatus.setName("CANCELLED");
        order.setStatus(cancelledStatus);

        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByName("CANCELLED")).thenReturn(Optional.of(cancelledStatus));

        ResponseEntity<String> response = userService.cancelOrder(1L, authentication);

        assertEquals(ResponseEntity.badRequest().body("This order is already cancelled!"), response);
    }

    @Test
    void testCancelOrder_Success() {
        User currentUser = new User();
        currentUser.setId(1L);
        lenient().when(authentication.getPrincipal()).thenReturn(myUserDetails);
        lenient().when(myUserDetails.getUser()).thenReturn(currentUser);
        Order order = new Order();
        order.setUser(currentUser);
        OrderStatus currentStatus = new OrderStatus();
        currentStatus.setName("IN_PROGRESS");
        order.setStatus(currentStatus);

        OrderStatus cancelledStatus = new OrderStatus();
        cancelledStatus.setName("CANCELLED");

        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByName("CANCELLED")).thenReturn(Optional.of(cancelledStatus));

        ResponseEntity<String> response = userService.cancelOrder(1L, authentication);

        assertEquals(ResponseEntity.ok("Order cancelled successfully!"), response);
    }

    @Test
    void testCancelOrder_ReturnProductsToStock_Success() {
        User currentUser = new User();
        currentUser.setId(1L);
        lenient().when(authentication.getPrincipal()).thenReturn(myUserDetails);
        lenient().when(myUserDetails.getUser()).thenReturn(currentUser);

        Order order = new Order();
        order.setId(1L);
        order.setUser(currentUser);
        OrderStatus currentStatus = new OrderStatus();
        currentStatus.setName("IN_PROGRESS");
        order.setStatus(currentStatus);

        OrderStatus cancelledStatus = new OrderStatus();
        cancelledStatus.setName("CANCELLED");

        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByName("CANCELLED")).thenReturn(Optional.of(cancelledStatus));

        Product product1 = new Product();
        product1.setId(1L);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setQuantity(20);

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setProduct(product1);
        orderProduct1.setQuantity(5);

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setProduct(product2);
        orderProduct2.setQuantity(10);

        List<OrderProduct> orderProducts = Arrays.asList(orderProduct1, orderProduct2);

        when(orderProductRepository.findAllByOrderId(order.getId())).thenReturn(orderProducts);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        ResponseEntity<String> response = userService.cancelOrder(order.getId(), authentication);

        assertEquals(ResponseEntity.ok("Order cancelled successfully!"), response);

        assertEquals(15, product1.getQuantity());
        assertEquals(30, product2.getQuantity());

        verify(productRepository).save(product1);
        verify(productRepository).save(product2);
    }

    @Test
    void testUpdateQuantity_ProductNotFound() throws Exception {
        Long productId = 1L;
        int quantity = 5;
        Mockito.when(authentication.getPrincipal()).thenReturn(new MyUserDetails(user));
        Mockito.when(productService.getBasketOrder(user)).thenReturn(Optional.of(new Order()));
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userService.updateQuantity(productId, quantity, authentication);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Product with id " + productId + " not found!", response.getBody());
    }

    @Test
    void testUpdateQuantity_OrderNotFound() throws Exception {
        Long productId = 1L;
        int quantity = 5;
        Mockito.when(authentication.getPrincipal()).thenReturn(new MyUserDetails(user));
        Mockito.when(productService.getBasketOrder(user)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userService.updateQuantity(productId, quantity, authentication);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Order not found!", response.getBody());
    }

    @Test
    void testUpdateQuantity_NegativeQuantity() throws Exception {
        Long productId = 1L;
        int quantity = -1;
        Mockito.when(authentication.getPrincipal()).thenReturn(new MyUserDetails(user));
        Mockito.when(productService.getBasketOrder(user)).thenReturn(Optional.of(new Order()));

        Product product = new Product();
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ResponseEntity<?> response = userService.updateQuantity(productId, quantity, authentication);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("The quantity cannot be negative number", response.getBody());
    }

    @Test
    void testUpdateQuantity_QuantityNotAvailable() throws Exception {
        Long productId = 1L;
        int newQuantity = 10;
        user.setId(1L);

        MyUserDetails myUserDetails = Mockito.mock(MyUserDetails.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(myUserDetails);
        Mockito.when(myUserDetails.getUser()).thenReturn(user);

        Order basketOrder = new Order();
        basketOrder.setId(1L);
        Mockito.when(productService.getBasketOrder(user)).thenReturn(Optional.of(basketOrder));

        Product product = new Product();
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        lenient().when(orderRepository.findById(basketOrder.getId())).thenReturn(Optional.of(basketOrder));

        lenient().when(orderProductRepository.findByOrderIdAndProductId(basketOrder.getId(), productId))
                .thenThrow(new QuantityNotAvailableException("The quantity that you are trying to set is not available!"));

        ResponseEntity<?> response = userService.updateQuantity(productId, newQuantity, authentication);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("The quantity that you are trying to set is not available!", response.getBody());
    }

    @Test
    void testUpdateQuantity_QuantityIsZero() throws Exception {
        Long productId = 1L;
        int newQuantity = 0;
        user.setId(1L);

        MyUserDetails myUserDetails = Mockito.mock(MyUserDetails.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(myUserDetails);
        Mockito.when(myUserDetails.getUser()).thenReturn(user);

        Order basketOrder = new Order();

        basketOrder.setId(1L);
        Mockito.when(productService.getBasketOrder(user)).thenReturn(Optional.of(basketOrder));
        Product product = new Product();
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        lenient().when(orderRepository.findById(basketOrder.getId())).thenReturn(Optional.of(basketOrder));

        OrderProduct orderProduct = Mockito.mock(OrderProduct.class);
        Mockito.when(orderProductRepository.findByOrderIdAndProductId(basketOrder.getId(), productId)).thenReturn(orderProduct);
        lenient().when(orderProduct.getProduct()).thenReturn(product);

        Long currentOrderProductId = 1L;
        Mockito.when(orderProductRepository.findByOrderIdAndProductId(basketOrder.getId(), productId)).thenReturn(orderProduct);
        Mockito.when(orderProduct.getId()).thenReturn(currentOrderProductId);

        ResponseEntity<?> response = userService.updateQuantity(productId, newQuantity, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(orderProductRepository).deleteById(currentOrderProductId);
    }

    @Test
    void testGetCurrentUserOrders_UserNotFound() {
        Long userId = 1L;

        MyUserDetails myUserDetails = Mockito.mock(MyUserDetails.class);
        User user = Mockito.mock(User.class);
        Mockito.when(myUserDetails.getUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(userId);
        Mockito.when(authentication.getPrincipal()).thenReturn(myUserDetails);

        ResponseEntity<List<UserOrdersResponse>> response = userService.getCurrentUserOrders(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetCurrentUserOrders_UserFound() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Address address = new Address();
        City city = new City();
        city.setName("Sofia");
        address.setCity(city);
        user.setAddress(address);

        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderDateTime(LocalDateTime.now());
        order1.setStatus(OrderStatus.builder().name("SHIPPED").build());

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderDateTime(LocalDateTime.now());
        order2.setStatus(new OrderStatus(OrderStatusType.SHIPPED.getId(), OrderStatusType.SHIPPED.name()));

        List<Order> orders = Arrays.asList(order1, order2);

        Mockito.when(orderRepository.findOrdersByUserIdAndStatusNotBasket(userId)).thenReturn(orders);

        Mockito.when(orderProductRepository.findAllByOrder_Id(order1.getId())).thenReturn(new ArrayList<>());
        Mockito.when(orderProductRepository.findAllByOrder_Id(order2.getId())).thenReturn(new ArrayList<>());

        Mockito.when(authentication.getPrincipal()).thenReturn(myUserDetails);
        Mockito.when(myUserDetails.getUser()).thenReturn(user);

        ResponseEntity<List<UserOrdersResponse>> response = userService.getCurrentUserOrders(authentication);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserOrdersResponse> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());

        UserOrdersResponse response1 = responseBody.get(0);
        assertEquals(order1.getId(), response1.getOrderId());
        assertEquals(order1.getOrderDateTime(), response1.getOrderDate());
        assertEquals(order1.getStatus().getName(), response1.getStatus());
        assertEquals(BigDecimal.ZERO, response1.getPrice());
        assertTrue(response1.getProducts().isEmpty());

        UserOrdersResponse response2 = responseBody.get(1);
        assertEquals(order2.getId(), response2.getOrderId());
        assertEquals(order2.getOrderDateTime(), response2.getOrderDate());
        assertEquals(order2.getStatus().getName(), response2.getStatus());
        assertEquals(BigDecimal.ZERO, response2.getPrice());
        assertTrue(response2.getProducts().isEmpty());
    }

    @Test
    void testGetBasket_NoBasketOrder() throws Exception {
        Authentication authentication = mock(Authentication.class);
        MyUserDetails myUserDetails = mock(MyUserDetails.class);
        User user = new User();
        user.setId(1L);

        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getUser()).thenReturn(user);

        when(productService.getBasketOrder(user)).thenReturn(Optional.empty());

        ResponseEntity<BasketResponse> response = userService.getBasket(authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BasketResponse basketResponse = response.getBody();
        assertNotNull(basketResponse);
        assertTrue(basketResponse.getProducts().isEmpty());
        assertEquals(BigDecimal.ZERO, basketResponse.getTotalPrice());
    }

    @Test
    void testGetBasket_Success() throws Exception {
        User currentUser = new User();
        currentUser.setId(1L);
        lenient().when(authentication.getPrincipal()).thenReturn(myUserDetails);
        lenient().when(myUserDetails.getUser()).thenReturn(currentUser);

        Order basketOrder = new Order();
        basketOrder.setId(1L);
        basketOrder.setUser(currentUser);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.valueOf(100));
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setPrice(BigDecimal.valueOf(200));
        product2.setQuantity(20);

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setOrder(basketOrder);
        orderProduct1.setProduct(product1);
        orderProduct1.setQuantity(2);
        orderProduct1.setProductPriceWhenPurchased(BigDecimal.valueOf(100));

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setOrder(basketOrder);
        orderProduct2.setProduct(product2);
        orderProduct2.setQuantity(1);
        orderProduct2.setProductPriceWhenPurchased(BigDecimal.valueOf(200));

        List<OrderProduct> orderProducts = Arrays.asList(orderProduct1, orderProduct2);

        when(productService.getBasketOrder(currentUser)).thenReturn(Optional.of(basketOrder));
        when(orderProductRepository.findAllByOrderId(basketOrder.getId())).thenReturn(orderProducts);
        when(orderProductRepository.findAllByOrder_Id(basketOrder.getId())).thenReturn(orderProducts);

        ResponseEntity<BasketResponse> response = userService.getBasket(authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BasketResponse basketResponse = response.getBody();
        assertNotNull(basketResponse);
        assertEquals(2, basketResponse.getProducts().size());
        assertEquals(BigDecimal.valueOf(400), basketResponse.getTotalPrice());  // (100 * 2) + (200 * 1)

        // Verify the basket products
        BasketProductResponseDTO productResponse1 = basketResponse.getProducts().get(0);
        assertEquals(product1.getId(), productResponse1.getId());
        assertEquals(orderProduct1.getQuantity(), productResponse1.getQuantity());
        assertEquals(product1.getPrice().multiply(BigDecimal.valueOf(orderProduct1.getQuantity())), productResponse1.getSubtotal());

        BasketProductResponseDTO productResponse2 = basketResponse.getProducts().get(1);
        assertEquals(product2.getId(), productResponse2.getId());
        assertEquals(orderProduct2.getQuantity(), productResponse2.getQuantity());
        assertEquals(product2.getPrice().multiply(BigDecimal.valueOf(orderProduct2.getQuantity())), productResponse2.getSubtotal());
    }
}

