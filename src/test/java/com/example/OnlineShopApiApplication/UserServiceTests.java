package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Controller.UserController;
import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Dto.Response.*;
import com.example.online_shop_api.Entity.*;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Exceptions.*;
import com.example.online_shop_api.Mapper.ProductMapper;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.Repository.*;
import com.example.online_shop_api.Service.OrderService;
import com.example.online_shop_api.Service.ProductService;
import com.example.online_shop_api.Service.UserService;
import com.example.online_shop_api.Utils.ValidationUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserServiceTests {
    @InjectMocks
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
    OrderProductRepository orderProductRepository;
    @Mock
    AddressRepository addressRepository;
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
    private MockedStatic<ProductMapper> mockedStaticProductMapper;
    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
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
        mockedStaticUserMapper.when(() -> UserMapper.toEntity(Mockito.any(UserRequestDto.class))).thenReturn(user);

        when(encoder.encode("pass123")).thenReturn("encodedPassword");

        String actualResult = userService.addNewUser(userRequestDto);

        assertEquals("Account created successfully!", actualResult);
        verify(addressRepository, times(1)).save(user.getAddress());
        verify(userRepository, times(1)).save(user);
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

        ResponseEntity<String> response = userService.registerNewUser(userRequestDto, bindingResult);

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

        ResponseEntity<String> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.badRequest().body("Email already in use. Please use a different email!"), response);
    }

    @Test
    void testRegisterNewUser_ThrowsUsernameInUseException() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userRequestDto.getUsername())).thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.badRequest().body("Username already in use. Please use a different username!"), response);
    }

    @Test
    void testRegisterNewUser_ThrowsPasswordsNotMatchingException() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        userRequestDto.setRepeatedPassword("password321");

        assertThrows(PasswordsNotMatchingException.class, () -> userService.validateNewUser(userRequestDto));

        ResponseEntity<String> actualResponse = userService.registerNewUser(userRequestDto, bindingResult);
        ResponseEntity<String> expectedResponse = ResponseEntity.badRequest().body("Passwords don't match");

        assertEquals(expectedResponse, actualResponse);
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

        ResponseEntity<String> actualResponse = userService.registerNewUser(userRequestDto, bindingResult);
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
        when(validationUtil.validateNotNullFields(userRequestDto)).thenReturn(Collections.singletonList("firstName was not entered"));

        ResponseEntity<String> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.badRequest().body("firstName was not entered"), response);
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
    void testUpdateQuantity_ProductNotFound() {
        Long orderId = 1L;
        Long productId = 1L;
        int newQuantity = 3;

        Order order = new Order();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userService.updateQuantity(productId, orderId, newQuantity);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product with id 1 not found!", response.getBody());
    }

    @Test
    void testUpdateQuantity_OrderNotFound() {
        Long orderId = 1L;
        Long productId = 1L;
        int newQuantity = 3;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userService.updateQuantity(productId, orderId, newQuantity);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Order with id 1 not found!", response.getBody());
    }

    @Test
    void testUpdateQuantity_NegativeQuantity() {
        Long orderId = 1L;
        Long productId = 1L;
        int newQuantity = -1;

        Order order = new Order();
        Product product = new Product();
        product.setQuantity(10);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ResponseEntity<?> response = userService.updateQuantity(productId, orderId, newQuantity);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The quantity cannot be negative number", response.getBody());
    }

    @Test
    void getBasketProducts_shouldReturnProductResponseDtos() throws Exception {

        Method method = UserService.class.getDeclaredMethod("getBasketProducts", Order.class);
        method.setAccessible(true);

        Order basketOrder = new Order();
        basketOrder.setId(1L);

        Product product = new Product();
        product.setPrice(new BigDecimal("20.00"));

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setQuantity(2);

        List<OrderProduct> orderProducts = List.of(orderProduct);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setQuantity(2);
        productResponseDto.setSubtotal(new BigDecimal("40.00"));

        when(orderProductRepository.findAllByOrderId(basketOrder.getId())).thenReturn(orderProducts);
        mockedStaticProductMapper.when(() -> ProductMapper.toDto(Mockito.any(Product.class))).thenReturn(productResponseDto);

        List<ProductResponseDto> result = (List<ProductResponseDto>) method.invoke(userService, basketOrder);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(new BigDecimal("40.00"), result.get(0).getSubtotal());
    }

    @Test
    void calculateTotalPrice_WithProducts_ShouldReturnCorrectTotal() throws Exception {
        Method method = UserService.class.getDeclaredMethod("calculateTotalPrice", List.class);
        method.setAccessible(true);

        ProductResponseDto product1 = new ProductResponseDto();
        product1.setPrice(new BigDecimal("10.50"));

        ProductResponseDto product2 = new ProductResponseDto();
        product2.setPrice(new BigDecimal("20.75"));

        List<ProductResponseDto> basketProducts = List.of(product1, product2);

        BigDecimal total = (BigDecimal) method.invoke(userService, basketProducts);

        assertEquals(new BigDecimal("31.25"), total, "Total price should be 31.25");
    }

    @Test
    void calculateTotalPrice_EmptyList_ShouldReturnZero() throws Exception {
        Method method = UserService.class.getDeclaredMethod("calculateTotalPrice", List.class);
        method.setAccessible(true);

        List<ProductResponseDto> basketProducts = List.of();

        BigDecimal total = (BigDecimal) method.invoke(userService, basketProducts);

        assertEquals(BigDecimal.ZERO, total, "Total price should be zero for an empty list");
    }

    @Test
    void calculateTotalPrice_WithZeroPriceProducts_ShouldReturnZero() throws Exception {
        Method method = UserService.class.getDeclaredMethod("calculateTotalPrice", List.class);
        method.setAccessible(true);

        ProductResponseDto product1 = new ProductResponseDto();
        product1.setPrice(BigDecimal.ZERO);

        ProductResponseDto product2 = new ProductResponseDto();
        product2.setPrice(BigDecimal.ZERO);

        List<ProductResponseDto> basketProducts = List.of(product1, product2);

        BigDecimal total = (BigDecimal) method.invoke(userService, basketProducts);

        assertEquals(BigDecimal.ZERO, total, "Total price should be zero when all products have zero price");
    }

    @Test
    void testValidateProductAvailability() throws Exception {
        // Arrange
        Product productInStock = new Product();
        productInStock.setId(1L);
        productInStock.setName("Test Product");
        productInStock.setQuantity(5);

        when(productRepository.findByIdNotDeleted(1L)).thenReturn(Optional.of(productInStock));

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(productInStock);
        orderProduct.setQuantity(10);

        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        Method method = UserService.class.getDeclaredMethod("validateProductAvailability", List.class);
        method.setAccessible(true);

        List<String> errors = (List<String>) method.invoke(userService, orderProducts);

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("The product Test Product has stock of 5 and you can not order amount of 10 of this product!");
        assertEquals(expectedErrors, errors, "Errors list should match the expected error message.");
    }

    @Test
    void testReduceStockQuantity() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setQuantity(20);

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setProduct(product1);
        orderProduct1.setQuantity(3);

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setProduct(product2);
        orderProduct2.setQuantity(5);

        List<OrderProduct> orderProducts = Arrays.asList(orderProduct1, orderProduct2);

        when(productRepository.findByIdNotDeleted(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findByIdNotDeleted(2L)).thenReturn(Optional.of(product2));

        Method method = UserService.class.getDeclaredMethod("reduceStockQuantity", List.class);
        method.setAccessible(true);
        method.invoke(userService, orderProducts);

        verify(productRepository, times(1)).findByIdNotDeleted(1L);
        verify(productRepository, times(1)).findByIdNotDeleted(2L);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(2)).save(productCaptor.capture());

        List<Product> savedProducts = productCaptor.getAllValues();
        assertEquals(7, savedProducts.get(0).getQuantity()); // 10 - 3 = 7
        assertEquals(15, savedProducts.get(1).getQuantity()); // 20 - 5 = 15
    }

    @Test
    void saveThePurchasePrice() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(new BigDecimal("10.00"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setPrice(new BigDecimal("20.00"));

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setId(1L);
        orderProduct1.setProduct(product1);

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct1.setId(2L);
        orderProduct2.setProduct(product2);

        List<OrderProduct> orderProducts = Arrays.asList(orderProduct1, orderProduct2);
        when(productRepository.findByIdNotDeleted(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findByIdNotDeleted(2L)).thenReturn(Optional.of(product2));

        Method method = UserService.class.getDeclaredMethod("saveThePurchasePriceInOrderProduct", List.class);
        method.setAccessible(true);
        method.invoke(userService, orderProducts);

        verify(productRepository, times(1)).findByIdNotDeleted(1L);
        verify(productRepository, times(1)).findByIdNotDeleted(2L);

        // Create an ArgumentCaptor to capture OrderProduct objects passed to the save method of orderProductRepository
        ArgumentCaptor<OrderProduct> orderProductCaptor = ArgumentCaptor.forClass(OrderProduct.class);

        // Verify that the save method of orderProductRepository is called exactly twice and capture the OrderProduct objects passed in each call
        verify(orderProductRepository, times(2)).save(orderProductCaptor.capture());

        List<OrderProduct> savedOrderProducts = orderProductCaptor.getAllValues();
        assertEquals(new BigDecimal("10.00"), savedOrderProducts.get(0).getProductPriceWhenPurchased());
        assertEquals(new BigDecimal("20.00"), savedOrderProducts.get(1).getProductPriceWhenPurchased());
    }

    @Test
    public void testCalculateTotalPriceAndFillProductResponses() throws Exception {
        List<ProductResponse> productResponses = new ArrayList<>();

        Product product1 = new Product();
        Product product2 = new Product();

        OrderProduct op1 = new OrderProduct();
        op1.setProduct(product1);
        op1.setProductPriceWhenPurchased(new BigDecimal("10.00"));
        op1.setQuantity(2);
        OrderProduct op2 = new OrderProduct();
        op2.setProduct(product2);
        op2.setProductPriceWhenPurchased(new BigDecimal("5.00"));
        op2.setQuantity(3);

        List<OrderProduct> products = List.of(op1, op2);

        Method method = UserService.class.getDeclaredMethod("calculateTotalPriceAndFillProductResponses", List.class, List.class);
        method.setAccessible(true);
        BigDecimal total = (BigDecimal) method.invoke(userService, products, productResponses);

        assertEquals(new BigDecimal("35.00"), total);
        assertEquals(2, productResponses.size());
    }

}
