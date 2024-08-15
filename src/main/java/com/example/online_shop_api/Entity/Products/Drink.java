package com.example.online_shop_api.Entity.Products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Drink extends Product {

    @Column(name = "best_before")
    @NotNull
    private LocalDate bestBefore;

    public Drink(String name, BigDecimal price, int quantity, List<String> imageUrls,LocalDate bestBefore) {
        super(name, price, quantity,imageUrls);
        this.bestBefore = bestBefore;
    }
}
