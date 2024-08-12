package com.example.online_shop_api.Dto.Response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditResponse {
    @Size(min = 3, max = 40, message = "First name must be between 3 and 40 characters")
    private String firstName;

    @Size(min = 3, max = 40, message = "Last name must be between 3 and 40 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @Size(min = 10, max = 30, message = "Email must be between 10 and 30 characters")
    private String email;

    private String city;

    @NotBlank(message = "Street name cannot be blank")
    private String streetName;

    private String phoneNumber;
}
