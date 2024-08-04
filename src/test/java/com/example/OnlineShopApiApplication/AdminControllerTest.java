package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Entity.Employee;
import com.example.online_shop_api.OnlineShopApiApplication;
import com.example.online_shop_api.Repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = OnlineShopApiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testShowEmployees() throws Exception {
        mockMvc.perform(get("/admin/show-employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Employee"))
                .andExpect(jsonPath("$[0].lastName").value("1"))
                .andExpect(jsonPath("$[0].salary").value("2000.0"))
                .andExpect(jsonPath("$[0].enabled").value("false"))
                .andExpect(jsonPath("$[1].firstName").value("Employee"))
                .andExpect(jsonPath("$[1].lastName").value("2"))
                .andExpect(jsonPath("$[1].salary").value("1500.0"))
                .andExpect(jsonPath("$[1].enabled").value("true"));
    }

    @Test
    void testUpdateEmployeeStatusAndSalary() throws Exception {
        Long employeeId = 1L;
        Boolean isEnabled = true;
        BigDecimal salary = new BigDecimal("3000.00");

        mockMvc.perform(put("/admin/update-employee/{id}", employeeId)
                        .param("isEnabled", isEnabled.toString())
                        .param("salary", salary.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee updated successfully"));

        Optional<Employee> optionalUpdatedEmployee = employeeRepository.findById(employeeId);
        if (optionalUpdatedEmployee.isPresent()) {
            Employee updatedEmployee = optionalUpdatedEmployee.get();
            assert updatedEmployee.isEnabled();
            assert updatedEmployee.getSalary().equals(salary);
        }
    }
}
