package com.example.OnlineShopApiApplication.Controllers;

import com.example.online_shop_api.OnlineShopApiApplication;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OnlineShopApiApplication.class)
 class ApplicationConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
     void testModelMapperBeanExists() {
        ModelMapper modelMapper = applicationContext.getBean(ModelMapper.class);
        assertThat(modelMapper).isNotNull();
    }
}
