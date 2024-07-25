package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Entity.Address;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.EmailInUseException;
import com.example.online_shop_api.Exceptions.PasswordsNotMatchingException;
import com.example.online_shop_api.Exceptions.ServerErrorException;
import com.example.online_shop_api.Exceptions.UsernameInUseException;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.Repository.AddressRepository;
import com.example.online_shop_api.Repository.UserRepository;
import com.example.online_shop_api.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserServiceTests {
    @InjectMocks
    private UserService userService;
    @InjectMocks
    private UserRequestDto userRequestDto;
    @Mock
    UserRepository userRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    BCryptPasswordEncoder encoder;
    @Mock
    UserMapper userMapper;

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
    void testAddNewUser_CorrectParameters(){
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        User user = new User();
        when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        when(encoder.encode("pass123")).thenReturn("encodedPassword");

        String actualResult = userService.addNewUser(userRequestDto);

        assertEquals("Account created successfully!", actualResult);
        verify(addressRepository,times(1)).save(user.getAddress());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddNewUser_ThrowException(){
        userRequestDto.setPassword("pass123");
        userRequestDto.setRepeatedPassword("pass123");
        when(userMapper.toEntity(userRequestDto)).thenThrow(new RuntimeException("Error"));

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

        User user = new User();
        user.setAddress(new Address());

        when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        when(encoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");

        ResponseEntity<String> response = userService.registerNewUser(userRequestDto, bindingResult);

        assertEquals(ResponseEntity.ok("Account created successfully!"), response);

        verify(addressRepository,times(1)).save(user.getAddress());
        verify(userRepository, times(1)).save(user);
    }
}
