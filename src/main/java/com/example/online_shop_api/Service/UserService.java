package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Entity.Role;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.EmailInUseException;
import com.example.online_shop_api.Exceptions.PasswordsNotMatchingException;
import com.example.online_shop_api.Exceptions.ServerErrorException;
import com.example.online_shop_api.Exceptions.UsernameInUseException;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.Repository.AddressRepository;
import com.example.online_shop_api.Repository.UserRepository;
import com.example.online_shop_api.Static.RoleType;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private AddressRepository addressRepository;
    private BCryptPasswordEncoder encoder;
    private UserMapper userMapper;

    private boolean isEmailInDB(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isUsernameInDB(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean arePasswordsMatching(UserRequestDto userRequestDto) {
        return userRequestDto.getPassword().equals(userRequestDto.getRepeatedPassword());
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
    }

    public String addNewUser(UserRequestDto userRequestDto) {
        Role userRole = new Role(RoleType.ROLE_USER.getId(), RoleType.ROLE_USER.name());
        try {
            User user = userMapper.toEntity(userRequestDto);
            addressRepository.save(user.getAddress());
            user.setRole(userRole);
            user.setPassword(encoder.encode(userRequestDto.getPassword()));
            userRepository.save(user);
            return "Account created successfully!";
        } catch (Exception exception) {
            throw new ServerErrorException("An internal error occurred. Please try again.");
        }
    }

    public ResponseEntity<String> registerNewUser(@Valid UserRequestDto userRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        try {
            validateNewUser(userRequestDto);
        } catch (EmailInUseException | UsernameInUseException | PasswordsNotMatchingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(addNewUser(userRequestDto));
    }

}
