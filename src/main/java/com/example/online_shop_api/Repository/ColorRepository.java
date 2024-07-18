package com.example.online_shop_api.Repository;

import com.example.online_shop_api.Entity.ProductHelpers.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByName(String name);
}
