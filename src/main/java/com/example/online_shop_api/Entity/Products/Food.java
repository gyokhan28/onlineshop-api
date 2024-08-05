package com.example.online_shop_api.Entity.Products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food extends Product {

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    public Food(String name, BigDecimal price, int quantity, LocalDate expiryDate, List<String> imageUrls) {
        super(name, price, quantity, imageUrls);
        this.expiryDate = expiryDate;
    }
}