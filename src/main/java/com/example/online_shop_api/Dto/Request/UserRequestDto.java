package com.example.online_shop_api.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRequestDto {
    @NotNull
    @Size(min = 3, max = 40, message = "First name must be between 3 and 40 characters long!")
    private String firstName;

    @NotNull
    @Size(min = 3, max = 40, message = "Last name must be between 3 and 40 characters long!")
    private String lastName;

    @NotNull
    @Size(min = 3, max = 30, message = "Username must be between 3 and 40 characters long!")
    private String username;

    @NotNull
    @Size(min = 10, max = 30, message = "E-mail must be between 10 and 30 characters long!")
    private String email;

    @NotBlank(message = "Please enter a password!")
    @Size(min = 3, message = "The password must be at least 3 characters long!")
    private String password;

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
