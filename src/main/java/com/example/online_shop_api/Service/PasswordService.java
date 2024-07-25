package com.example.online_shop_api.Service;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.Exceptions.IncorrectPasswordException;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean isUser(MyUserDetails myUserDetails) {
        return myUserDetails.getUser() != null;
    }

    public boolean isCurrentPasswordCorrect(MyUserDetails myUserDetails, String passwordToCheck) {
        String currentPassword;
        if (isUser(myUserDetails)) {
            currentPassword = myUserDetails.getUser().getPassword();
        } else {
            currentPassword = myUserDetails.getEmployee().getPassword();
        }
        return bCryptPasswordEncoder.matches(passwordToCheck, currentPassword);
    }

    public void updatePassword(MyUserDetails myUserDetails, String newPassword) {
        if (isUser(myUserDetails)) {
            User user = myUserDetails.getUser();
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            Employee employee = myUserDetails.getEmployee();
            employee.setPassword(bCryptPasswordEncoder.encode(newPassword));
            employeeRepository.save(employee);
        }
    }

    private void validateNewPassword(String newPassword, String repeatNewPassword) {
        if (!newPassword.equals(repeatNewPassword)) {
            throw new IncorrectPasswordException("New password and confirm password do not match!");
        }
        if (newPassword.isEmpty() || newPassword.isBlank()) {
            throw new IncorrectPasswordException("The new password cannot be empty!");
        }
        if (newPassword.length() < 3) {
            throw new IncorrectPasswordException("The new password cannot be less than 3 symbols long!");
        }
    }

    public ResponseEntity<String> changePassword(String currentPassword, String newPassword, String repeatNewPassword, Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        if (!isCurrentPasswordCorrect(myUserDetails, currentPassword)) {
            return ResponseEntity.badRequest().body("Incorrect current password!");
        }
        try {
            validateNewPassword(newPassword, repeatNewPassword);
        } catch (IncorrectPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        updatePassword(myUserDetails, newPassword);
        return ResponseEntity.ok("The password was changed successfully!");
    }
}
