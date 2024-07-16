package com.example.online_shop_api.Repository;

import com.example.online_shop_api.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByPhoneNumber(String phoneNumber);
    Optional<Employee> findByUsername(String username);
    List<Employee> findByRole_IdNot(Long roleId);
}
