package com.example.online_shop_api.Repository;

import com.example.online_shop_api.Entity.ProductHelpers.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByName(String name);
}
