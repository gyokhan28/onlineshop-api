package com.example.online_shop_api.Entity.Products;


import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Decoration extends Product {

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public Decoration(String name, BigDecimal price, int quantity, Material material, Brand brand, List<String> imageUrls) {
        super(name, price, quantity, imageUrls);
        this.material = material;
        this.brand = brand;
    }
}
