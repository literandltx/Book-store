package com.example.bookstore.repository;

import com.example.bookstore.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User users INNER JOIN FETCH users.roles WHERE users.email = :email")
    Optional<User> findByEmail(String email);
}
