package com.example.online_shop_api.Entity.Products;

import com.project.Onlineshop.Entity.ProductHelpers.Material;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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


    public Sanitary(String name, BigDecimal price, int quantity, boolean isBiodegradable, boolean isReusable, Material material, String imageLocation) {
        super(name, price, quantity, imageLocation);
        this.isBiodegradable = isBiodegradable;
        this.isReusable = isReusable;
        this.material = material;
    }
}
