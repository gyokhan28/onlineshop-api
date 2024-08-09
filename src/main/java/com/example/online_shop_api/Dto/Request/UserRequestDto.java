package com.example.online_shop_api.Dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {
    @NotNull(message = "firstName was not entered")
    @Size(min = 3, max = 40, message = "First name must be between 3 and 40 characters long!")
    private String firstName;

    @NotNull(message = "lastName was not entered")
    @Size(min = 3, max = 40, message = "Last name must be between 3 and 40 characters long!")
    private String lastName;

    @NotNull(message = "username was not entered")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 40 characters long!")
    private String username;

    @NotNull(message = "email was not entered")
    @Email(message = "Invalid email format.")
    @Size(min = 10, max = 30, message = "E-mail must be between 10 and 30 characters long!")
    private String email;

    @NotBlank(message = "Please enter a password!")
    @Size(min = 3, message = "The password must be at least 3 characters long!")
    @NotNull(message = "password was not entered")
    private String password;

    @NotNull(message = "repeatedPassword was not entered")
    @NotBlank(message = "Please enter a confirmation password!")
    @Size(min = 3, message = "The password must be at least 3 characters long!")
    private String repeatedPassword;

    @NotNull(message = "Please select a city!")
    private Long cityId;

    @NotBlank(message = "Please enter a Street name!")
    private String streetName;

    private String additionalInformation;

    private String phoneNumber;
}
