package com.example.online_shop_api.Dto.Request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeRequestDto {
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @Column(unique = true)
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format.")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @NotEmpty(message = "Password cannot be empty")
    private String repeatedPassword;

    private LocalDate dateOfBirth;
    private BigDecimal salary;

    @Column(unique = true, nullable = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private String jobType;
}
