package com.example.online_shop_api.Entity.Products;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

//@MappedSuperclass
//@NoArgsConstructor
//@AllArgsConstructor
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product")
@SuperBuilder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Long id;
    protected String name;
    protected BigDecimal price;
    protected int quantity;

    protected String imageLocation;
    protected boolean isDeleted;

    public Product() {
    }

    public Product(String name, BigDecimal price, int quantity, String imageLocation) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageLocation = imageLocation;
        this.isDeleted = false;
    }

    public void setImageLocation(String imageLocation) {
        // Set the image to null if not set in the frontend so that it doesn't show up an empty/missing picture
        if (imageLocation.isEmpty()) {
            imageLocation = null;
        }
        this.imageLocation = imageLocation;
    }
}