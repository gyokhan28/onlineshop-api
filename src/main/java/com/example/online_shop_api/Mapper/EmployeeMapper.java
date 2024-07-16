package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Response.EmployeeResponseDto;
import com.example.online_shop_api.Entity.Employee;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class EmployeeMapper {

    public EmployeeResponseDto toDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();

        employeeResponseDto.setDateOfBirth(employee.getDateOfBirth());
        employeeResponseDto.setId(employee.getId());
        employeeResponseDto.setFirstName(employee.getFirstName());
        employeeResponseDto.setLastName(employee.getLastName());
        employeeResponseDto.setUsername(employee.getUsername());
        employeeResponseDto.setEmail(employee.getEmail());
        employeeResponseDto.setSalary(employee.getSalary());
        employeeResponseDto.setPhoneNumber(employee.getPhoneNumber());
        employeeResponseDto.setRole(employee.getRole());
        employeeResponseDto.setCreatedAt(employee.getCreatedAt());
        employeeResponseDto.setEnabled(employee.isEnabled());
        employeeResponseDto.setAge(calculateAge(employee.getDateOfBirth()));

        return employeeResponseDto;
    }

    public Employee toEntity(EmployeeRequestDto employeeRequestDto) {
        if(employeeRequestDto == null){
            return null;
        }

        Employee.EmployeeBuilder employee = Employee.builder();

        employee.firstName(employeeRequestDto.getFirstName());
        employee.lastName(employeeRequestDto.getLastName());
        employee.username(employeeRequestDto.getUsername());
        employee.email(employeeRequestDto.getEmail());
        employee.password(employeeRequestDto.getPassword());
        employee.dateOfBirth(employeeRequestDto.getDateOfBirth());
        employee.salary(employeeRequestDto.getSalary());
        employee.phoneNumber(employeeRequestDto.getPhoneNumber());

        employee.createdAt(java.time.LocalDateTime.now());
        employee.isEnabled(true);

        return employee.build();
    }

    public int calculateAge(LocalDate birthDate) {
        if (birthDate != null) {
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        }
        return 0;
    }
}
