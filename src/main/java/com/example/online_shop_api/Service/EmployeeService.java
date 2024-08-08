package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Response.EmployeeResponseDto;
import com.example.online_shop_api.Dto.Response.ErrorResponse;
import com.example.online_shop_api.Dto.Response.SuccessResponse;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.JobType;
import com.example.online_shop_api.Entity.Role;
import com.example.online_shop_api.Exceptions.*;
import com.example.online_shop_api.Mapper.EmployeeMapper;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.JobTypeRepository;
import com.example.online_shop_api.Repository.RoleRepository;
import com.example.online_shop_api.Static.JobTypeEnum;
import com.example.online_shop_api.Static.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final JobTypeRepository jobTypeRepository;
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        try {
            List<EmployeeResponseDto> employeeResponseDtos = EmployeeMapper.toDtoList(employeeRepository.findAll());
            return ResponseEntity.ok(employeeResponseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployeesExceptAdmins() {
        try {
            List<EmployeeResponseDto> employeeResponseDtos = EmployeeMapper.toDtoList(employeeRepository.findByRole_IdNot(1L));
            return ResponseEntity.ok(employeeResponseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<?> registerNewEmployee(EmployeeRequestDto employeeRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            create(employeeRequestDto);
        } catch (EmailInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("email-error", e.getMessage()));
        } catch (UsernameInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("username-error", e.getMessage()));
        } catch (PasswordsNotMatchingException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("password-error", e.getMessage()));
        } catch (PhoneInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("phone-error", e.getMessage()));
        } catch (ServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("server-error", e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("Account created successfully!"));
    }

    public void create(EmployeeRequestDto employeeRequestDto) {
        if (isEmailInDB(employeeRequestDto.getEmail())) {
            throw new EmailInUseException("Email already in use. Please use a different email");
        }
        if (employeeRequestDto.getPhoneNumber() != null && isPhoneNumberInDB(employeeRequestDto.getPhoneNumber())) {
            throw new PhoneInUseException("Phone number already in use. Please use a different phone number or leave blank");
        }
        if (isUsernameInDB(employeeRequestDto.getUsername())) {
            throw new UsernameInUseException("Username already in use. Please use a different username");
        }
        if(!arePasswordsMatching(employeeRequestDto)) {
            throw new PasswordsNotMatchingException("Passwords don't match");
        }

        Optional<Role> optionalRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name());
        if (optionalRole.isEmpty()) {
            throw new ServerErrorException("Employee role not found in the DB");
        }
        String jobTypeName = JobTypeEnum.valueOf(employeeRequestDto.getJobType()).toString();
        Optional<JobType> optionalJobType = jobTypeRepository.findByName(jobTypeName);
        if (optionalJobType.isEmpty()) {
            throw new ServerErrorException("Job Type not found in the DB");
        }

        try {
            Employee employee = EmployeeMapper.toEntity(employeeRequestDto);
            employee.setRole(optionalRole.get());
            //employee.setPassword(encoder.encode(employeeRequestDto.getPassword()));
            employee.setJobType(optionalJobType.get());
            //new employee accounts will be disabled -> after admin approval, they will be enabled.
            employee.setEnabled(false);
            employeeRepository.save(employee);
            EmployeeMapper.toDto(employee);
        } catch (Exception exception) {
            throw new ServerErrorException("An internal error occurred. Please try again. " + exception.getMessage());
        }
    }

    private boolean isEmailInDB(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }

    private boolean isPhoneNumberInDB(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
    private boolean isUsernameInDB(String username){
        return employeeRepository.findByUsername(username).isPresent();
    }
    private boolean arePasswordsMatching(EmployeeRequestDto employeeRequestDto) {
        return employeeRequestDto.getPassword().equals(employeeRequestDto.getRepeatedPassword());
    }
}
