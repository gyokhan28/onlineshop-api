package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.UserRequestDto;
import com.example.online_shop_api.Entity.Role;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.*;
import com.example.online_shop_api.Mapper.UserMapper;
import com.example.online_shop_api.Repository.AddressRepository;
import com.example.online_shop_api.Repository.CityRepository;
import com.example.online_shop_api.Repository.UserRepository;
import com.example.online_shop_api.Static.RoleType;
import com.example.online_shop_api.Utils.ValidationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserMapper userMapper;
    private final CityRepository cityRepository;
    private final ValidationUtil validationUtil;
    private boolean isEmailInDB(String email) {
        return userRepository.findByEmail(email).isPresent();
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

    public ResponseEntity<String> registerNewUser(@Valid @RequestBody UserRequestDto userRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        List<String> validationErrors = validationUtil.validateNotNullFields(userRequestDto);
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(String.join(", ", validationErrors));
        }
        try {
            validateNewUser(userRequestDto);
        } catch (EmailInUseException | UsernameInUseException | PasswordsNotMatchingException |
                 CityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(addNewUser(userRequestDto));
    }

}
