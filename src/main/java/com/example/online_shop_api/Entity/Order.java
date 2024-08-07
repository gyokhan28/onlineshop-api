package com.example.online_shop_api.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDateTime;

    private LocalDateTime orderDeliveryDateTime;
    private LocalDateTime orderCancelDateTime;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> products;
}
