package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.OnlineShopApiApplication;
import com.example.online_shop_api.Repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = OnlineShopApiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Test
    void testShowOrders() throws Exception {
        mockMvc.perform(get("/orders/show")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userResponseDto.username").value("user"))
                .andExpect(jsonPath("$[0].orderDateTime").value("2024-07-17T22:51:47"))
                .andExpect(jsonPath("$[0].status").value("SHIPPED"))
                .andExpect(jsonPath("$[0].orderProducts.length()").value(2))
                .andExpect(jsonPath("$[0].orderProducts[0].productName").value("Voda Bankq"))
                .andExpect(jsonPath("$[0].orderProducts[0].quantity").value(2))
                .andExpect(jsonPath("$[0].orderProducts[0].productPriceWhenPurchased").value(0.85))
                .andExpect(jsonPath("$[0].orderProducts[1].productName").value("Boza"))
                .andExpect(jsonPath("$[0].orderProducts[1].quantity").value(4))
                .andExpect(jsonPath("$[0].orderProducts[1].productPriceWhenPurchased").value(0.95))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userResponseDto.username").value("user"))
                .andExpect(jsonPath("$[1].orderDateTime").value("2024-07-20T22:51:47"))
                .andExpect(jsonPath("$[1].status").value("PROCESSING"))
                .andExpect(jsonPath("$[1].orderProducts.length()").value(0));
    }
}
