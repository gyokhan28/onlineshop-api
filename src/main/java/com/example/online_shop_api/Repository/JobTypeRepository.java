package com.example.online_shop_api.Repository;

import com.example.online_shop_api.Entity.JobType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobTypeRepository extends JpaRepository<JobType, Long> {
    Optional<JobType> findByName(String name);
}
