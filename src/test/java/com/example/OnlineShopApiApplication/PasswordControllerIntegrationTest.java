package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.Entity.User;
import com.example.online_shop_api.MyUserDetails;
import com.example.online_shop_api.OnlineShopApiApplication;
import com.example.online_shop_api.Repository.EmployeeRepository;
import com.example.online_shop_api.Repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(classes = OnlineShopApiApplication.class) // Specify the configuration class
@AutoConfigureMockMvc
class PasswordControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Container
    static MySQLContainer<?> mySqlContainer = new MySQLContainer("mysql:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        mySqlContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySqlContainer.stop();
    }

    @BeforeEach
    void setUp() {
        User user = userRepository.findByUsername("user").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        MyUserDetails myUserDetails = new MyUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities()));
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

    @Test
    void testUsersInDatabase() throws Exception {
        String jdbcUrl = mySqlContainer.getJdbcUrl();
        String username = mySqlContainer.getUsername();
        String password = mySqlContainer.getPassword();

        boolean userFound = false;
        boolean user2Found = false;

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

            while (resultSet.next()) {
                String userName = resultSet.getString("username");
                if ("user".equals(userName)) {
                    userFound = true;
                } else if ("user2".equals(userName)) {
                    user2Found = true;
                }
            }

            assertTrue(userFound, "User 'user' should be present in the database");
            assertTrue(user2Found, "User 'user2' should be present in the database");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred while checking users in the database: " + e.getMessage());
        }


//        Thread.sleep(300000); // 5 minutes delay so that DB can be tested manually
    }

}
