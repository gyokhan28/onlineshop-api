package com.example.online_shop_api.Entity.Products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
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
