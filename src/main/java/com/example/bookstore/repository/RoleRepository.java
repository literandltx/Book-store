package com.example.bookstore.repository;

import com.example.bookstore.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByName(Role.RoleName name);
}
