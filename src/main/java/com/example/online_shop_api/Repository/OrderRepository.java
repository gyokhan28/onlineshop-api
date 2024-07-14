package com.example.online_shop_api.Repository;

import com.example.online_shop_api.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser_IdAndStatus_Id(Long userId, Long statusId);
    List<Order> findByUserIdAndStatusName(Long userId, String statusName);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status.id != 0")
    List<Order> findOrdersByUserIdAndStatusNotBasket(Long userId);
}
