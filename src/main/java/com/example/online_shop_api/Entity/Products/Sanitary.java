package com.example.online_shop_api.Entity.Products;

import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sanitary extends Product {
    @Column
    private boolean isBiodegradable;

    @Column
    private boolean isReusable;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;


    public Sanitary(String name, BigDecimal price, int quantity, boolean isBiodegradable, boolean isReusable, Material material) {
        super(name, price, quantity);
        this.isBiodegradable = isBiodegradable;
        this.isReusable = isReusable;
        this.material = material;
    }
}
