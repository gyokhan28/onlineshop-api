package com.example.OnlineShopApiApplication.Controllers;

import com.example.online_shop_api.Controller.ProductHelpersController;
import com.example.online_shop_api.OnlineShopApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = OnlineShopApiApplication.class)
class ProductHelpersControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testProductHelpersControllerBeanExists() {
        ProductHelpersController productHelpersController = applicationContext.getBean(ProductHelpersController.class);
        assertThat(productHelpersController).isNotNull();
    }
}
