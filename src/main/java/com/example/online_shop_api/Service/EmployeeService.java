package com.example.online_shop_api.Service;

import com.example.online_shop_api.Dto.Request.EmployeeRequestDto;
import com.example.online_shop_api.Dto.Response.ErrorResponse;
import com.example.online_shop_api.Dto.Response.SuccessResponse;
import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.Role;
import com.example.online_shop_api.Exceptions.*;
import com.example.online_shop_api.Mapper.*;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.RoleRepository;
import com.example.online_shop_api.Static.JobType;
import com.example.online_shop_api.Static.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder encoder;

    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findByRole_IdNot(1L));
    }

    public ResponseEntity<EmployeeRequestDto> getNewEmployee() {
        return ResponseEntity.ok(new EmployeeRequestDto());
    }

    public ResponseEntity<?> registerNewEmployee(EmployeeRequestDto employeeRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            create(employeeRequestDto);
        } catch (EmailInUseException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("email-error", e.getMessage()));
        } catch (UsernameInUseException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("username-error", e.getMessage()));
        } catch (PasswordsNotMatchingException e){
            return ResponseEntity.badRequest().body(new ErrorResponse("password-error", e.getMessage()));
        } catch (PhoneInUseException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("phone-error", e.getMessage()));
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

        validatePasswordsAreMatching(employeeRequestDto);

        Optional<Role> optionalRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name());
        if (optionalRole.isEmpty()) {
            throw new ServerErrorException("Employee role not found in the DB");
        }

        try {
            Employee employee = employeeMapper.toEntity(employeeRequestDto);
            employee.setRole(optionalRole.get());
            employee.setPassword(encoder.encode(employeeRequestDto.getPassword()));
            employee.setJobType(JobType.valueOf(employeeRequestDto.getJobType()));
            //new employee accounts will be disabled -> after admin approval, they will be enabled.
            employee.setEnabled(false);
            employeeRepository.save(employee);
            employeeMapper.toDto(employee);
        } catch (Exception exception) {
            throw new ServerErrorException("An internal error occurred. Please try again. " + exception.getMessage());
        }
    }

    private void validatePasswordsAreMatching(EmployeeRequestDto employeeRequestDto) {
        if (!employeeRequestDto.getPassword().equals(employeeRequestDto.getRepeatedPassword())) {
            throw new PasswordsNotMatchingException("Passwords don't match");
        }
    }

    private boolean isEmailInDB(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }

    private boolean isPhoneNumberInDB(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public ResponseEntity<MyUserDetails> showEmployeeProfile(Authentication authentication){
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userDetails);
    }
}
