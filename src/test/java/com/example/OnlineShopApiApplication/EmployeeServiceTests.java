package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Response.EmployeeResponseDto;
import com.example.online_shop_api.Dto.Response.ErrorResponse;
import com.example.online_shop_api.Dto.Response.SuccessResponse;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.JobType;
import com.example.online_shop_api.Entity.Role;
import com.example.online_shop_api.Mapper.EmployeeMapper;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.JobTypeRepository;
import com.example.online_shop_api.Repository.RoleRepository;
import com.example.online_shop_api.Service.EmployeeService;
import com.example.online_shop_api.Static.RoleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {
    @InjectMocks
    EmployeeService employeeService;

    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    JobTypeRepository jobTypeRepository;
    @Mock
    BindingResult bindingResult;
    @Test
    void testGetAllEmployeesSuccess() {
        Role role = Role.builder().id(2L).name("ROLE_EMPLOYEE").build();
        JobType jobType = JobType.builder().id(2L).name("CASHIER").build();
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setRole(role);
        employee.setJobType(jobType);
        List<Employee> employees = List.of(employee);
        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeResponseDto> expectedDtoList = EmployeeMapper.toDtoList(employees);

        ResponseEntity<List<EmployeeResponseDto>> response = employeeService.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtoList, response.getBody());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEmployeesException() {
        when(employeeRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<EmployeeResponseDto>> response = employeeService.getAllEmployees();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(employeeRepository,times(1)).findAll();
    }

    @Test
    void testGetAllEmployeesExceptAdminsSuccess() {
        Role employeeRole = Role.builder().id(2L).name("ROLE_EMPLOYEE").build();
        Role adminRole = Role.builder().id(1L).name("ROLE_ADMIN").build();
        JobType jobType = JobType.builder().id(2L).name("CASHIER").build();
        JobType admin = JobType.builder().id(0L).name("ADMIN").build();

        Employee adminEmployee = new Employee();
        adminEmployee.setId(1L);
        adminEmployee.setFirstName("Admin");
        adminEmployee.setRole(adminRole);
        adminEmployee.setJobType(admin);

        Employee employee = new Employee();
        employee.setId(2L);
        employee.setFirstName("John");
        employee.setRole(employeeRole);
        employee.setJobType(jobType);

        List<Employee> employees = List.of(employee, adminEmployee);
        when(employeeRepository.findByRole_IdNot(1L)).thenReturn(employees);

        List<EmployeeResponseDto> expectedDtoList = EmployeeMapper.toDtoList(employees);

        ResponseEntity<List<EmployeeResponseDto>> response = employeeService.getAllEmployeesExceptAdmins();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtoList, response.getBody());
        verify(employeeRepository, times(1)).findByRole_IdNot(1L);
    }

    @Test
    void testGetAllEmployeesExceptAdminsException() {
        when(employeeRepository.findByRole_IdNot(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<EmployeeResponseDto>> response = employeeService.getAllEmployeesExceptAdmins();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(employeeRepository,times(1)).findByRole_IdNot(1L);
    }

    @Test
    void testRegisterNewEmployee_WithBindingErrors() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();

        ObjectError error1 = new ObjectError("field1", "Field1 error message");
        ObjectError error2 = new ObjectError("field2", "Field2 error message");
        List<ObjectError> errors = Arrays.asList(error1, error2);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        ResponseEntity<?> response = employeeService.registerNewEmployee(employeeRequestDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof List<?>);
        assertEquals(2, ((List) response.getBody()).size());
    }

    @Test
    void testRegisterNewEmployee_ThrowsEmailInUseException() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();
        String email = "test@mail.bg";
        employeeRequestDto.setEmail(email);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(new Employee()));

        ResponseEntity<?> response = employeeService.registerNewEmployee(employeeRequestDto, bindingResult);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Email already in use. Please use a different email", errorResponse.getMessage());
    }

    @Test
    void testRegisterNewEmployee_ThrowsUsernameInUseException() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();
        String userName = "username";
        employeeRequestDto.setUsername(userName);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(employeeRepository.findByUsername(userName)).thenReturn(Optional.of(new Employee()));

        ResponseEntity<?> response = employeeService.registerNewEmployee(employeeRequestDto, bindingResult);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Username already in use. Please use a different username", errorResponse.getMessage());
    }

    @Test
    void testRegisterNewEmployee_ThrowsPhoneInUseException() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();
        String phoneNumber = "333-555";
        employeeRequestDto.setPhoneNumber(phoneNumber);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(employeeRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(new Employee()));

        ResponseEntity<?> response = employeeService.registerNewEmployee(employeeRequestDto, bindingResult);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Phone number already in use. Please use a different phone number or leave blank", errorResponse.getMessage());
    }

    @Test
    void testRegisterNewEmployee_ThrowsPasswordsNotMatchingException() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();
        String username = "test";
        String password = "aaa";
        String repeatedPassword = "bbb";
        employeeRequestDto.setUsername(username);
        employeeRequestDto.setPassword(password);
        employeeRequestDto.setRepeatedPassword(repeatedPassword);

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = employeeService.registerNewEmployee(employeeRequestDto, bindingResult);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Passwords don't match", errorResponse.getMessage());
    }

    @Test
    void testRegisterNewEmployee_ThrowsRoleNotFoundException() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();
        String username = "test";
        employeeRequestDto.setUsername(username);
        employeeRequestDto.setPassword("aaa");
        employeeRequestDto.setRepeatedPassword("aaa");
        employeeRequestDto.setEmail("someEmail@example.com");
        employeeRequestDto.setPhoneNumber("123-123");

        when(bindingResult.hasErrors()).thenReturn(false);

        when(employeeRepository.findByEmail("someEmail@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(employeeRepository.findByPhoneNumber("123-123")).thenReturn(Optional.empty());

        when(roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name())).thenReturn(Optional.empty());

        ResponseEntity<?> response = employeeService.registerNewEmployee(employeeRequestDto, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Employee role not found in the DB", errorResponse.getMessage());
    }

    @Test
    void testRegisterNewEmployee_ThrowsJobTypeNotFoundException() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();
        String username = "test";
        employeeRequestDto.setUsername(username);
        employeeRequestDto.setPassword("aaa");
        employeeRequestDto.setRepeatedPassword("aaa");
        employeeRequestDto.setEmail("someEmail@example.com");
        employeeRequestDto.setPhoneNumber("123-123");
        Role role = new Role();
        employeeRequestDto.setJobType("CASHIER");

        when(bindingResult.hasErrors()).thenReturn(false);

        when(employeeRepository.findByEmail("someEmail@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(employeeRepository.findByPhoneNumber("123-123")).thenReturn(Optional.empty());

        when(roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name())).thenReturn(Optional.of(role));
        when(jobTypeRepository.findByName(employeeRequestDto.getJobType())).thenReturn(Optional.empty());


        ResponseEntity<?> response = employeeService.registerNewEmployee(employeeRequestDto, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Job Type not found in the DB", errorResponse.getMessage());
    }

    @Test
    void testRegisterNewEmployee_Success() {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto();
        String username = "test";
        employeeRequestDto.setUsername(username);
        employeeRequestDto.setPassword("aaa");
        employeeRequestDto.setRepeatedPassword("aaa");
        employeeRequestDto.setEmail("someEmail@example.com");
        employeeRequestDto.setPhoneNumber("123-123");
        Role role = new Role();
        JobType jobType = new JobType();
        employeeRequestDto.setJobType("CASHIER");

        when(bindingResult.hasErrors()).thenReturn(false);

        when(employeeRepository.findByEmail("someEmail@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(employeeRepository.findByPhoneNumber("123-123")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name())).thenReturn(Optional.of(role));
        when(jobTypeRepository.findByName(employeeRequestDto.getJobType())).thenReturn(Optional.of(jobType));

        ResponseEntity<?> response = employeeService.registerNewEmployee(employeeRequestDto, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        SuccessResponse successResponse = (SuccessResponse) response.getBody();
        assertEquals("Account created successfully!", successResponse.getMessage());

        assertEquals(username, employeeRequestDto.getUsername());
        assertEquals("someEmail@example.com", employeeRequestDto.getEmail());
        assertEquals("123-123", employeeRequestDto.getPhoneNumber());
        assertEquals("CASHIER", employeeRequestDto.getJobType());
    }
}
