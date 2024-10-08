package com.example.online_shop_api.Entity.Products;


import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Decoration extends Product {

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public Decoration(String name, BigDecimal price, int quantity, Material material, Brand brand) {
        super(name, price, quantity);
        this.material = material;
        this.brand = brand;
    }
}
