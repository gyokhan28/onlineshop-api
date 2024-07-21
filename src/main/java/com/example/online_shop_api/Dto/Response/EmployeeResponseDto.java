package com.example.online_shop_api.Dto.Response;

import com.example.online_shop_api.Entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EmployeeResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private LocalDate dateOfBirth;
    private int age;
    private BigDecimal salary;
    private String phoneNumber;
    private String role;
    private LocalDateTime createdAt;
    private boolean isEnabled;
    private String jobType;
}
