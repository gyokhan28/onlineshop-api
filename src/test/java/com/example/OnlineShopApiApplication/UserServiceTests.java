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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private ProductService productService;
    @Mock
    private Authentication authentication;
    @Mock
    private User user;
    @Mock
    private MyUserDetails myUserDetails;
    @Mock
    private OrderProductRepository orderProductRepository;

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
    public void testGetBasketWithNoBasketOrder() {
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getUser()).thenReturn(user);
        when(productService.getBasketOrder(user)).thenReturn(null);

        ResponseEntity<BasketResponse> response = userService.getBasket(authentication);

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 200);
        BasketResponse basketResponse = response.getBody();
        assertNotNull(basketResponse);
        assertEquals(basketResponse.getProducts().size(), 0);
        assertEquals(basketResponse.getTotalPrice(), BigDecimal.ZERO);
    }

    @Test
    public void testGetBasketWithBasketOrder() {
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getUser()).thenReturn(user);

        Order basketOrder = mock(Order.class);
        when(productService.getBasketOrder(user)).thenReturn(basketOrder);

        List<OrderProduct> orderProducts = new ArrayList<>();
        OrderProduct orderProduct1 = mock(OrderProduct.class);
        Product product1 = mock(Product.class);
        when(orderProduct1.getProduct()).thenReturn(product1);
        when(product1.getPrice()).thenReturn(BigDecimal.TEN);
        when(orderProduct1.getQuantity()).thenReturn(1);
        orderProducts.add(orderProduct1);

        OrderProduct orderProduct2 = mock(OrderProduct.class);
        Product product2 = mock(Product.class);
        when(orderProduct2.getProduct()).thenReturn(product2);
        when(product2.getPrice()).thenReturn(BigDecimal.valueOf(20));
        when(orderProduct2.getQuantity()).thenReturn(2);
        orderProducts.add(orderProduct2);

        when(orderProductRepository.findAllByOrderId(basketOrder.getId())).thenReturn(orderProducts);

        ResponseEntity<BasketResponse> response = userService.getBasket(authentication);

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 200);
        BasketResponse basketResponse = response.getBody();
        assertNotNull(basketResponse);
        assertEquals(basketResponse.getProducts().size(), 2);
        assertEquals(basketResponse.getTotalPrice(), BigDecimal.valueOf(50));
    }
}

