package com.example.online_shop_api.Entity.Products;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Drink extends Product {

    @Column(name = "best_before")
    @NotNull
    private LocalDate bestBefore;

    public Drink(String name, BigDecimal price, int quantity, LocalDate bestBefore) {
        super(name, price, quantity);
        this.bestBefore = bestBefore;
    }
}
