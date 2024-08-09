package com.example.online_shop_api.Repository;

import com.example.online_shop_api.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AddressRepository extends JpaRepository<Address, Long> {
}
