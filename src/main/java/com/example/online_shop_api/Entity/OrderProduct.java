package com.example.online_shop_api.Entity;

import com.example.online_shop_api.Entity.Products.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private Order order;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private BigDecimal productPriceWhenPurchased;

    public OrderProduct(Long id, String name, BigDecimal price, int quantity) {
        this.product = Product.builder().id(id).name(name).price(price).build();
        this.quantity = quantity;
    }
}