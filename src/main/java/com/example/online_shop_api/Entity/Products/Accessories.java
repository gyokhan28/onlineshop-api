package com.example.online_shop_api.Entity.Products;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("Accessories")
public class Accessories extends Product {

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public Accessories(String name, BigDecimal price, int quantity, Color color, Brand brand, List<String> imageUrls) {
        super(name, price, quantity, imageUrls);
        this.color = color;
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "Accessories{" +
                "color=" + color +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}