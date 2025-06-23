package com.stefan.ecommerce.repositories;

import com.stefan.ecommerce.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNameIgnoreCase(String name);
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
} 