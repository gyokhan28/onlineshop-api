package com.example.online_shop_api.Entity.Products;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("Drink")
public class Drink extends Product {

    @Column(name = "best_before")
    private LocalDate bestBefore;

    public Drink(String name, BigDecimal price, int quantity, LocalDate bestBefore, List<String> imageUrls) {
        super(name, price, quantity, imageUrls);
        this.bestBefore = bestBefore;
    }
}
