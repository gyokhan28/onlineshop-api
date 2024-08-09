package com.example.online_shop_api;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "bearer-key",
		description = "authorization with JWT token", scheme = "bearer",
		bearerFormat = "JWT")
public class OnlineShopApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineShopApiApplication.class, args);
	}

}
