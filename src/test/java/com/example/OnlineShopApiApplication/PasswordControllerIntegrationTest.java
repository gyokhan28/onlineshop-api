package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.OnlineShopApiApplication;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OnlineShopApiApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PasswordControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    private static MyUserDetails myUserDetailsForUser;

    @BeforeAll
    static void setup(@Autowired UserRepository userRepository) {
        User user = userRepository.findByUsername("user").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        myUserDetailsForUser = new MyUserDetails(user);
    }

    @BeforeEach
    void setupSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(myUserDetailsForUser, null, myUserDetailsForUser.getAuthorities()));
    }

    @Test
    void changePasswordTest() throws Exception {
        String currentPassword = "123";
        String newPassword = "123";
        String confirmPassword = "123";

        mockMvc.perform(put("/password/update")
                        .param("currentPassword", currentPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("The password was changed successfully!"));
    }

    @Test
    void changePasswordTestEmployee() throws Exception {
        Employee employee = employeeRepository.findByUsername("admin").orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

        MyUserDetails myUserDetails = new MyUserDetails(employee);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities()));

        String currentPassword = "123";
        String newPassword = "newPass123";
        String confirmPassword = "newPass123";

        mockMvc.perform(put("/password/update")
                        .param("currentPassword", currentPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("The password was changed successfully!"));
    }

    @Test
    void testPasswordsDoNotMatch() throws Exception {
        String currentPassword = "123";
        String newPassword = "newPassword";
        String confirmPassword = "differentPassword";

        mockMvc.perform(put("/password/update")
                        .param("currentPassword", currentPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("New password and confirm password do not match!"));
    }

    @Test
    void testEmptyNewPassword() throws Exception {
        String currentPassword = "123";
        String newPassword = "";
        String confirmPassword = "";

        mockMvc.perform(put("/password/update")
                        .param("currentPassword", currentPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The new password cannot be empty!"));
    }

    @Test
    void testPasswordLessThanThreeSymbols() throws Exception {
        String currentPassword = "123";
        String newPassword = "a";
        String confirmPassword = "a";

        mockMvc.perform(put("/password/update")
                        .param("currentPassword", currentPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The new password cannot be less than 3 symbols long!"));
    }

    @Test
    void testIncorrectCurrentPassword() throws Exception {
        Long userId = 1L;
        String currentPassword = "wrongPassword";
        String newPassword = "123";
        String confirmPassword = "123";

        mockMvc.perform(put("/password/update")
                        .param("userId", String.valueOf(userId))
                        .param("currentPassword", currentPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Incorrect current password!"));
    }
}
