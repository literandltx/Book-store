package com.example.bookstore.repository;

import com.example.bookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE CartItem c SET c.isDeleted = true WHERE c.shoppingCart.id = :userId")
    void deleteByShoppingCartIAndId(Long userId);
}
