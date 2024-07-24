package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.Role;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminServiceTests {
    private AdminService adminService;
    @Mock
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;
    private Role testRole;

    @BeforeEach
    public void setUp() {
        adminService = new AdminService(employeeRepository);

        testRole = new Role();
        testRole.setId(2L);
        testRole.setName("ROLE_EMPLOYEE");

        testEmployee = Employee.builder()
                .id(1L)
                .firstName("Test1")
                .lastName("Test1")
                .username("TestUsername")
                .email("test@mail.bg")
                .password("TestPassword")
                .isEnabled(false)
                .role(testRole)
                .build();
    }

    @Test
    public void testGetAllEmployees() {
        Employee testEmployee2 = Employee.builder()
                .id(2L)
                .firstName("Test2")
                .lastName("Test2")
                .username("TestUsername2")
                .email("test2@mail.bg")
                .password("TestPassword2")
                .role(testRole)
                .build();

        List<Employee> employeeList = Arrays.asList(testEmployee, testEmployee2);

        when(employeeRepository.findByRole_IdNot(1L)).thenReturn(employeeList);
        ResponseEntity<List<Employee>> response = adminService.getAllEmployees();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(employeeList, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(employeeRepository, times(1)).findByRole_IdNot(1L);
    }

    @Test
    public void testChangeEmployeeToEnabled() {

        when(employeeRepository.findById(1L)).thenReturn(Optional.ofNullable(testEmployee));

        assertFalse(testEmployee.isEnabled());

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("Employee updated successfully");
        ResponseEntity<String> actualResponse = adminService.updateEmployeeStatusAndSalary(1L, true, null);

        assertTrue(testEmployee.isEnabled());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testChangeEmployeeSalaryGivenIncorrectParameter() {

        when(employeeRepository.findById(1L)).thenReturn(Optional.ofNullable(testEmployee));
        ResponseEntity<String> expectedResponse = ResponseEntity.badRequest().body("Invalid salary format");
        ResponseEntity<String> actualResponse = adminService.updateEmployeeStatusAndSalary(1L, null, "not-a-number");

        assertEquals(expectedResponse, actualResponse);
        verify(employeeRepository, never()).findByRole_IdNot(1L);
    }

    @Test
    public void testChangeEmployeeSalaryAndStatusWithCorrectParameters() {

        when(employeeRepository.findById(1L)).thenReturn(Optional.ofNullable(testEmployee));
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("Employee updated successfully");
        ResponseEntity<String> actualResponse = adminService.updateEmployeeStatusAndSalary(1L, true, "200");

        assertTrue(testEmployee.isEnabled());
        assertEquals(BigDecimal.valueOf(200), testEmployee.getSalary());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testChangeEmployeeSalaryWithNotExistingEmployee() {
        ResponseEntity<String> expectedResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        ResponseEntity<String> actualResponse = adminService.updateEmployeeStatusAndSalary(3L, true, "200");

        assertEquals(expectedResponse, actualResponse);
        verify(employeeRepository, never()).findByRole_IdNot(3L);
    }
}
