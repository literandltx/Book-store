package com.example.bookstore.repository;

import com.example.bookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Modifying
    @Query("UPDATE CartItem c SET c.isDeleted = true WHERE c.shoppingCart.id = :userId")
    void deleteByShoppingCartIAndId(Long userId);
}
