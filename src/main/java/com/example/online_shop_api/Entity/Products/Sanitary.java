package com.example.online_shop_api.Entity.Products;

import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
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


    public Sanitary(String name, BigDecimal price, int quantity, List<String> imageUrls , boolean isBiodegradable, boolean isReusable, Material material) {
        super(name, price, quantity,imageUrls);
        this.isBiodegradable = isBiodegradable;
        this.isReusable = isReusable;
        this.material = material;
    }
}
